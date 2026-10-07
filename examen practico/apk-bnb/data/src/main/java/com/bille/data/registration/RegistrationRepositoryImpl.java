package com.bille.data.registration;

import com.bille.domain.location.LocationGateway;
import com.bille.domain.model.RegistrationForm;
import com.bille.domain.model.RegistrationReceipt;
import com.bille.domain.registration.LocationNotAuthorizedException;
import com.bille.domain.registration.RegistrationRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RegistrationRepositoryImpl implements RegistrationRepository {

    private final LocationGateway locationGateway;
    private final OnboardingService onboardingService;

    @Inject
    public RegistrationRepositoryImpl(LocationGateway locationGateway, OnboardingService onboardingService) {
        this.locationGateway = locationGateway;
        this.onboardingService = onboardingService;
    }

    @Override
    public RegistrationReceipt register(RegistrationForm form) {
        if (!locationGateway.hasLocationPermission() || !locationGateway.isLocationEnabled()) {
            throw new LocationNotAuthorizedException();
        }
        return onboardingService.consume(form, locationGateway.readFix());
    }
}
