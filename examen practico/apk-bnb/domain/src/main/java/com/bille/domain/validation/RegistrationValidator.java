package com.bille.domain.validation;

import com.bille.domain.model.RegistrationForm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reglas de la prueba:
 * celular, máximo 8 dígitos; carnet, 10 dígitos; complemento, 2 letras o números.
 */
public final class RegistrationValidator {

    public static final int PHONE_MAX_LENGTH = 8;
    public static final int CARNET_LENGTH = 10;
    public static final int COMPLEMENT_LENGTH = 2;

    public ValidationResult validate(RegistrationForm form) {
        Map<Field, FieldErrorCode> errors = new LinkedHashMap<>();

        FieldErrorCode phoneError = digitsError(form.phone(), 1, PHONE_MAX_LENGTH);
        if (phoneError != null) {
            errors.put(Field.PHONE, phoneError);
        }

        FieldErrorCode carnetError = digitsError(form.carnet(), CARNET_LENGTH, CARNET_LENGTH);
        if (carnetError != null) {
            errors.put(Field.CARNET, carnetError);
        }

        if (requiresComplement(form)) {
            FieldErrorCode complementError = alphanumericError(form.complement(), COMPLEMENT_LENGTH);
            if (complementError != null) {
                errors.put(Field.COMPLEMENT, complementError);
            }
        }

        return new ValidationResult(errors);
    }

    public static boolean requiresComplement(RegistrationForm form) {
        return form.hasComplement() || !form.complement().isEmpty();
    }

    public static String digitsOnly(String value, int maxLength) {
        StringBuilder builder = new StringBuilder();
        if (value == null) {
            return "";
        }
        for (int i = 0; i < value.length() && builder.length() < maxLength; i++) {
            char character = value.charAt(i);
            if (character >= '0' && character <= '9') {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    public static String alphanumericOnly(String value, int maxLength) {
        StringBuilder builder = new StringBuilder();
        if (value == null) {
            return "";
        }
        for (int i = 0; i < value.length() && builder.length() < maxLength; i++) {
            char character = value.charAt(i);
            if (isAsciiLetterOrDigit(character)) {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    private static FieldErrorCode digitsError(String value, int minLength, int maxLength) {
        if (value.isEmpty()) {
            return FieldErrorCode.REQUIRED;
        }
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character < '0' || character > '9') {
                return FieldErrorCode.INVALID_CHARACTERS;
            }
        }
        if (value.length() < minLength || value.length() > maxLength) {
            return FieldErrorCode.INVALID_LENGTH;
        }
        return null;
    }

    private static FieldErrorCode alphanumericError(String value, int length) {
        if (value.isEmpty()) {
            return FieldErrorCode.REQUIRED;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!isAsciiLetterOrDigit(value.charAt(i))) {
                return FieldErrorCode.INVALID_CHARACTERS;
            }
        }
        if (value.length() != length) {
            return FieldErrorCode.INVALID_LENGTH;
        }
        return null;
    }

    private static boolean isAsciiLetterOrDigit(char character) {
        return (character >= '0' && character <= '9')
                || (character >= 'A' && character <= 'Z')
                || (character >= 'a' && character <= 'z');
    }
}
