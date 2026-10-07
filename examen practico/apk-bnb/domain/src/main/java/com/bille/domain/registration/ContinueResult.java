package com.bille.domain.registration;

import com.bille.domain.model.RegistrationReceipt;
import com.bille.domain.validation.ValidationResult;

public sealed interface ContinueResult {

    record Invalid(ValidationResult validation) implements ContinueResult {
    }

    record LocationRequired(boolean permissionMissing, boolean locationDisabled) implements ContinueResult {
    }

    record Success(RegistrationReceipt receipt) implements ContinueResult {
    }
}
