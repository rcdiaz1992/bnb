package com.bille.app.information;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bille.app.R;
import com.bille.app.authentication.AuthenticationDestination;
import com.bille.app.common.AlphanumericInputFilter;
import com.bille.app.common.DigitsInputFilter;
import com.bille.app.common.Event;
import com.bille.app.common.SimpleTextWatcher;
import com.bille.app.common.UserMessage;
import com.bille.app.databinding.FragmentInformationBinding;
import com.bille.domain.validation.Field;
import com.bille.domain.validation.RegistrationValidator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InformationFragment extends Fragment implements EnableLocationBottomSheet.Listener {

    private static final String LOCATION_SHEET = "enable_location";

    private FragmentInformationBinding binding;
    private InformationViewModel viewModel;
    private boolean rendering;
    private boolean permissionRequested;
    private ActivityResultLauncher<String[]> permissionLauncher;
    private ActivityResultLauncher<Intent> settingsLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                this::onPermissionResult
        );
        settingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> viewModel.onNext()
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInformationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InformationViewModel.class);
        setupHeader();
        setupInputs();
        setupInsets();
        binding.buttonNext.setOnClickListener(v -> {
            hideKeyboard();
            viewModel.onNext();
        });
        viewModel.getUiState().observe(getViewLifecycleOwner(), this::render);
        viewModel.getEffects().observe(getViewLifecycleOwner(), this::handleEffect);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onContinueWithLocation() {
        LocationAccess access = viewModel.locationAccess();
        if (!access.permissionGranted()) {
            requestLocationPermission();
            return;
        }
        if (!access.locationEnabled()) {
            settingsLauncher.launch(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }
        viewModel.onNext();
    }

    private void setupHeader() {
        binding.stepHeader.render(getString(R.string.step_information), 1, getString(R.string.step_information), R.drawable.ic_step_info);
        binding.stepHeader.setOnBackClick(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupInputs() {
        binding.inputPhone.setFilters(new InputFilter[]{new DigitsInputFilter(RegistrationValidator.PHONE_MAX_LENGTH)});
        binding.inputCarnet.setFilters(new InputFilter[]{new DigitsInputFilter(RegistrationValidator.CARNET_LENGTH)});
        binding.inputComplement.setFilters(new InputFilter[]{new AlphanumericInputFilter(RegistrationValidator.COMPLEMENT_LENGTH)});

        binding.inputPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!rendering) {
                    viewModel.onPhoneChanged(s.toString());
                }
            }
        });
        binding.inputCarnet.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!rendering) {
                    viewModel.onCarnetChanged(s.toString());
                }
            }
        });
        binding.inputComplement.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!rendering) {
                    viewModel.onComplementChanged(s.toString());
                }
            }
        });
        binding.checkComplement.setOnCheckedChangeListener((button, checked) -> {
            if (!rendering) {
                viewModel.onHasComplementChanged(checked);
                if (checked) {
                    binding.inputComplement.requestFocus();
                }
            }
        });
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentPanel, (v, insets) -> {
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime()).bottom;
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottom);
            return insets;
        });
    }

    private void render(InformationUiState state) {
        rendering = true;
        setTextIfNeeded(binding.inputPhone, state.phone());
        setTextIfNeeded(binding.inputCarnet, state.carnet());
        setTextIfNeeded(binding.inputComplement, state.complement());
        if (binding.checkComplement.isChecked() != state.hasComplement()) {
            binding.checkComplement.setChecked(state.hasComplement());
        }
        showError(binding.phoneLayout, state, Field.PHONE, R.string.error_phone);
        showError(binding.carnetLayout, state, Field.CARNET, R.string.error_carnet);
        showError(binding.complementLayout, state, Field.COMPLEMENT, R.string.error_complement);
        bindCounter(binding.phoneCounter, state.phone().length(), RegistrationValidator.PHONE_MAX_LENGTH, state.errors().containsKey(Field.PHONE), isComplete(state.phone(), 1, RegistrationValidator.PHONE_MAX_LENGTH));
        bindCounter(binding.carnetCounter, state.carnet().length(), RegistrationValidator.CARNET_LENGTH, state.errors().containsKey(Field.CARNET), state.carnet().length() == RegistrationValidator.CARNET_LENGTH);
        binding.complementGroup.setVisibility(state.hasComplement() ? View.VISIBLE : View.GONE);
        bindCounter(binding.complementCounter, state.complement().length(), RegistrationValidator.COMPLEMENT_LENGTH, state.errors().containsKey(Field.COMPLEMENT), state.complement().length() == RegistrationValidator.COMPLEMENT_LENGTH);
        boolean busy = state.submitting();
        binding.inputPhone.setEnabled(!busy);
        binding.inputCarnet.setEnabled(!busy);
        binding.inputComplement.setEnabled(!busy);
        binding.checkComplement.setEnabled(!busy);
        binding.buttonNext.setEnabled(state.canContinue());
        binding.buttonNext.setText(busy ? R.string.action_saving : R.string.action_next);
        rendering = false;
    }

    private void handleEffect(Event<InformationEffect> event) {
        InformationEffect effect = event.getContentIfNotHandled();
        if (effect == null || binding == null) {
            return;
        }
        if (effect instanceof InformationEffect.ShowLocationPrompt) {
            if (getChildFragmentManager().findFragmentByTag(LOCATION_SHEET) == null) {
                new EnableLocationBottomSheet().show(getChildFragmentManager(), LOCATION_SHEET);
            }
            return;
        }
        if (effect instanceof InformationEffect.OpenAuthentication open) {
            Bundle args = new Bundle();
            args.putString(AuthenticationDestination.RECEIPT_ID, open.receiptId());
            NavHostFragment.findNavController(this).navigate(R.id.action_information_to_authentication, args);
            return;
        }
        if (effect instanceof InformationEffect.ShowMessage message) {
            int text = message.message() == UserMessage.SAVE_FAILED
                    ? R.string.save_failed
                    : R.string.location_permission_required;
            Snackbar.make(binding.getRoot(), text, Snackbar.LENGTH_LONG).show();
        }
    }

    private void requestLocationPermission() {
        boolean rationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionRequested && !rationale) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
            settingsLauncher.launch(intent);
            return;
        }
        permissionRequested = true;
        permissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void onPermissionResult(Map<String, Boolean> result) {
        boolean granted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION))
                || Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
        viewModel.onLocationPermissionResult(granted);
    }

    private void showError(TextInputLayout layout, InformationUiState state, Field field, int messageRes) {
        if (state.errors().containsKey(field)) {
            layout.setError(getString(messageRes));
            return;
        }
        layout.setError(null);
        layout.setErrorEnabled(false);
    }

    private void bindCounter(android.widget.TextView counter, int length, int max, boolean hasError, boolean complete) {
        counter.setText(getString(R.string.field_counter, length, max));
        int color = hasError ? R.color.bille_error : complete ? R.color.bille_green : R.color.bille_text_secondary;
        counter.setTextColor(ContextCompat.getColor(requireContext(), color));
    }

    private boolean isComplete(String value, int min, int max) {
        return value.length() >= min && value.length() <= max;
    }

    private void setTextIfNeeded(android.widget.EditText editText, String value) {
        if (!editText.getText().toString().equals(value)) {
            editText.setText(value);
            editText.setSelection(value.length());
        }
    }

    private void hideKeyboard() {
        if (binding == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        }
    }
}
