package com.bille.domain.validation;

import java.util.Collections;
import java.util.Map;

public record ValidationResult(Map<Field, FieldErrorCode> errors) {

    public ValidationResult {
        errors = errors == null ? Map.of() : Map.copyOf(errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public FieldErrorCode errorFor(Field field) {
        return errors.get(field);
    }

    public static ValidationResult ok() {
        return new ValidationResult(Collections.emptyMap());
    }
}
