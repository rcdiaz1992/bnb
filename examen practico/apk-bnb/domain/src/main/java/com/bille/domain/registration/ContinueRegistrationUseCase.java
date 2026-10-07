package com.bille.domain.registration;

import com.bille.domain.location.LocationGateway;
import com.bille.domain.model.RegistrationForm;
import com.bille.domain.validation.RegistrationValidator;
import com.bille.domain.validation.ValidationResult;

public final class ContinueRegistrationUseCase {

    private final RegistrationValidator validator;
    private final RegistrationRepository repository;

    public ContinueRegistrationUseCase(
            RegistrationValidator validator,
            RegistrationRepository repository
    ) {
        this.validator = validator;
        this.repository = repository;
    }

    public ContinueResult execute(RegistrationForm form, LocationGateway location) {
        ValidationResult validation = validator.validate(form);
        if (!validation.isValid()) {
            return new ContinueResult.Invalid(validation);
        }

        boolean permissionMissing = !location.hasLocationPermission();
        boolean locationDisabled = !location.isLocationEnabled();
        if (permissionMissing || locationDisabled) {
            return new ContinueResult.LocationRequired(permissionMissing, locationDisabled);
        }

        return new ContinueResult.Success(repository.register(form));
    }
}
