package com.bille.app.information;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.bille.app.common.Event;
import com.bille.app.common.UserMessage;
import com.bille.domain.location.LocationGateway;
import com.bille.domain.model.RegistrationForm;
import com.bille.domain.registration.ContinueRegistrationUseCase;
import com.bille.domain.registration.ContinueResult;
import com.bille.domain.validation.Field;
import com.bille.domain.validation.FieldErrorCode;
import com.bille.domain.validation.RegistrationValidator;
import com.bille.domain.validation.ValidationResult;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class InformationViewModel extends ViewModel {

    private static final String KEY_PHONE = "phone";
    private static final String KEY_CARNET = "carnet";
    private static final String KEY_COMPLEMENT = "complement";
    private static final String KEY_HAS_COMPLEMENT = "hasComplement";

    private final SavedStateHandle savedState;
    private final ContinueRegistrationUseCase continueRegistration;
    private final RegistrationValidator validator;
    private final LocationGateway locationGateway;
    private final MutableLiveData<InformationUiState> uiState = new MutableLiveData<>();
    private final MutableLiveData<Event<InformationEffect>> effects = new MutableLiveData<>();
    private final Set<Field> dirty = EnumSet.noneOf(Field.class);
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler mainThread = new Handler(Looper.getMainLooper());
    private boolean submitted;
    private boolean submitting;
    private boolean cleared;

    @Inject
    public InformationViewModel(
            SavedStateHandle savedState,
            ContinueRegistrationUseCase continueRegistration,
            RegistrationValidator validator,
            LocationGateway locationGateway
    ) {
        this.savedState = savedState;
        this.continueRegistration = continueRegistration;
        this.validator = validator;
        this.locationGateway = locationGateway;
        publish();
    }

    public LiveData<InformationUiState> getUiState() {
        return uiState;
    }

    public LiveData<Event<InformationEffect>> getEffects() {
        return effects;
    }

    public LocationAccess locationAccess() {
        return new LocationAccess(
                locationGateway.hasLocationPermission(),
                locationGateway.isLocationEnabled()
        );
    }

    public void onPhoneChanged(String value) {
        updateText(KEY_PHONE, RegistrationValidator.digitsOnly(value, RegistrationValidator.PHONE_MAX_LENGTH), phone(), Field.PHONE);
    }

    public void onCarnetChanged(String value) {
        updateText(KEY_CARNET, RegistrationValidator.digitsOnly(value, RegistrationValidator.CARNET_LENGTH), carnet(), Field.CARNET);
    }

    public void onComplementChanged(String value) {
        String sanitized = RegistrationValidator.alphanumericOnly(value, RegistrationValidator.COMPLEMENT_LENGTH);
        boolean markComplement = sanitized.isEmpty() ? hasComplement() : true;
        if (sanitized.equals(complement()) && markComplement == hasComplement() && dirty.contains(Field.COMPLEMENT)) {
            return;
        }
        savedState.set(KEY_COMPLEMENT, sanitized);
        savedState.set(KEY_HAS_COMPLEMENT, markComplement);
        dirty.add(Field.COMPLEMENT);
        publish();
    }

    public void onHasComplementChanged(boolean checked) {
        if (checked == hasComplement()) {
            return;
        }
        savedState.set(KEY_HAS_COMPLEMENT, checked);
        if (!checked) {
            savedState.set(KEY_COMPLEMENT, "");
            dirty.remove(Field.COMPLEMENT);
        } else {
            dirty.add(Field.COMPLEMENT);
        }
        publish();
    }

    public void onNext() {
        if (submitting) {
            return;
        }
        submitted = true;
        RegistrationForm form = currentForm();
        ValidationResult validation = validator.validate(form);
        if (!validation.isValid()) {
            publish();
            return;
        }
        if (!locationAccess().isReady()) {
            publish();
            effects.setValue(new Event<>(new InformationEffect.ShowLocationPrompt()));
            return;
        }
        submitting = true;
        publish();
        io.execute(() -> {
            ContinueResult result;
            try {
                result = continueRegistration.execute(form, locationGateway);
            } catch (RuntimeException exception) {
                result = null;
            }
            ContinueResult finished = result;
            mainThread.post(() -> deliverResult(finished));
        });
    }

    public void onLocationPermissionResult(boolean granted) {
        if (!granted) {
            effects.setValue(new Event<>(new InformationEffect.ShowMessage(UserMessage.PERMISSION_DENIED)));
            return;
        }
        onNext();
    }

    @Override
    protected void onCleared() {
        cleared = true;
        io.shutdownNow();
        super.onCleared();
    }

    private void deliverResult(ContinueResult result) {
        if (cleared) {
            return;
        }
        submitting = false;
        if (result == null) {
            publish();
            effects.setValue(new Event<>(new InformationEffect.ShowMessage(UserMessage.SAVE_FAILED)));
            return;
        }
        if (result instanceof ContinueResult.Invalid) {
            publish();
            return;
        }
        if (result instanceof ContinueResult.LocationRequired) {
            publish();
            effects.setValue(new Event<>(new InformationEffect.ShowLocationPrompt()));
            return;
        }
        if (result instanceof ContinueResult.Success success) {
            publish();
            effects.setValue(new Event<>(new InformationEffect.OpenAuthentication(success.receipt().id())));
        }
    }

    private void updateText(String key, String sanitized, String current, Field field) {
        if (sanitized.equals(current) && dirty.contains(field)) {
            return;
        }
        savedState.set(key, sanitized);
        dirty.add(field);
        publish();
    }

    private RegistrationForm currentForm() {
        return new RegistrationForm(phone(), carnet(), hasComplement(), complement());
    }

    private void publish() {
        ValidationResult validation = validator.validate(currentForm());
        Map<Field, FieldErrorCode> visibleErrors = new LinkedHashMap<>();
        for (Map.Entry<Field, FieldErrorCode> entry : validation.errors().entrySet()) {
            if (submitted || dirty.contains(entry.getKey())) {
                visibleErrors.put(entry.getKey(), entry.getValue());
            }
        }
        uiState.setValue(new InformationUiState(
                phone(),
                carnet(),
                complement(),
                hasComplement(),
                Map.copyOf(visibleErrors),
                validation.isValid() && !submitting,
                submitting
        ));
    }

    private String phone() {
        return text(KEY_PHONE);
    }

    private String carnet() {
        return text(KEY_CARNET);
    }

    private String complement() {
        return text(KEY_COMPLEMENT);
    }

    private String text(String key) {
        String value = savedState.get(key);
        return value == null ? "" : value;
    }

    private boolean hasComplement() {
        return Boolean.TRUE.equals(savedState.get(KEY_HAS_COMPLEMENT));
    }
}
