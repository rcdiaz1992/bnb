package com.bille.data.registration;

import com.bille.domain.location.LocationFix;
import com.bille.domain.location.LocationGateway;
import com.bille.domain.model.RegistrationForm;
import com.bille.domain.model.RegistrationReceipt;
import com.bille.domain.registration.LocationNotAuthorizedException;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegistrationRepositoryImplTest {

    private final RegistrationForm form = new RegistrationForm("71234567", "1234567890", false, "");

    @Test
    public void blocksTheServiceWhenLocationIsNotAuthorized() {
        RecordingService service = new RecordingService();
        RegistrationRepositoryImpl repository = new RegistrationRepositoryImpl(new FakeLocation(false, true), service);

        try {
            repository.register(form);
        } catch (LocationNotAuthorizedException expected) {
            assertFalse(service.called);
            return;
        }
        throw new AssertionError("El servicio no debe consumirse sin ubicación");
    }

    @Test
    public void consumesTheServiceWhenLocationIsAuthorized() {
        RecordingService service = new RecordingService();
        RegistrationRepositoryImpl repository = new RegistrationRepositoryImpl(new FakeLocation(true, true), service);

        RegistrationReceipt receipt = repository.register(form);

        assertTrue(service.called);
        assertFalse(receipt.id().isBlank());
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
            return new LocationFix(true, -16.5d, -68.15d);
        }
    }

    private static final class RecordingService implements OnboardingService {
        private boolean called;

        @Override
        public RegistrationReceipt consume(RegistrationForm form, LocationFix location) {
            called = true;
            return new RegistrationReceipt("service-1");
        }
    }
}
