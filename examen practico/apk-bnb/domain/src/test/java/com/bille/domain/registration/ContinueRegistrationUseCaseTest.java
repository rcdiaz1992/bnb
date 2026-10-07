package com.bille.domain.registration;

import com.bille.domain.location.LocationFix;
import com.bille.domain.location.LocationGateway;
import com.bille.domain.model.RegistrationForm;
import com.bille.domain.model.RegistrationReceipt;
import com.bille.domain.validation.RegistrationValidator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContinueRegistrationUseCaseTest {

    private final RegistrationForm validForm = new RegistrationForm("71234567", "1234567890", true, "1D");

    @Test
    public void doesNotConsumeTheServiceWhenPermissionIsMissing() {
        RecordingRepository repository = new RecordingRepository();
        ContinueRegistrationUseCase useCase = new ContinueRegistrationUseCase(new RegistrationValidator(), repository);

        ContinueResult result = useCase.execute(validForm, new FakeLocation(false, true));

        assertTrue(result instanceof ContinueResult.LocationRequired);
        assertTrue(((ContinueResult.LocationRequired) result).permissionMissing());
        assertFalse(repository.called);
    }

    @Test
    public void doesNotConsumeTheServiceWhenDeviceLocationIsOff() {
        RecordingRepository repository = new RecordingRepository();
        ContinueRegistrationUseCase useCase = new ContinueRegistrationUseCase(new RegistrationValidator(), repository);

        ContinueResult result = useCase.execute(validForm, new FakeLocation(true, false));

        assertTrue(result instanceof ContinueResult.LocationRequired);
        assertTrue(((ContinueResult.LocationRequired) result).locationDisabled());
        assertFalse(repository.called);
    }

    @Test
    public void consumesTheServiceOnlyAfterLocationIsAuthorized() {
        RecordingRepository repository = new RecordingRepository();
        ContinueRegistrationUseCase useCase = new ContinueRegistrationUseCase(new RegistrationValidator(), repository);

        ContinueResult result = useCase.execute(validForm, new FakeLocation(true, true));

        assertTrue(result instanceof ContinueResult.Success);
        assertTrue(repository.called);
    }

    @Test
    public void reportsFieldErrorsBeforeAskingForLocation() {
        RecordingRepository repository = new RecordingRepository();
        ContinueRegistrationUseCase useCase = new ContinueRegistrationUseCase(new RegistrationValidator(), repository);

        ContinueResult result = useCase.execute(
                new RegistrationForm("12", "123", false, ""),
                new FakeLocation(false, false)
        );

        assertTrue(result instanceof ContinueResult.Invalid);
        assertFalse(repository.called);
    }

    private static final class FakeLocation implements LocationGateway {
        private final boolean permission;
        private final boolean enabled;

        private FakeLocation(boolean permission, boolean enabled) {
            this.permission = permission;
            this.enabled = enabled;
        }

        @Override
        public boolean hasLocationPermission() {
            return permission;
        }

        @Override
        public boolean isLocationEnabled() {
            return enabled;
        }

        @Override
        public LocationFix readFix() {
            return LocationFix.unavailable();
        }
    }

    private static final class RecordingRepository implements RegistrationRepository {
        private boolean called;

        @Override
        public RegistrationReceipt register(RegistrationForm form) {
            called = true;
            return new RegistrationReceipt("receipt-1");
        }
    }
}
