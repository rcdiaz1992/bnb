package com.bille.domain.validation;

import com.bille.domain.model.RegistrationForm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegistrationValidatorTest {

    private final RegistrationValidator validator = new RegistrationValidator();

    @Test
    public void acceptsPhoneUpToEightDigitsCarnetOfTenAndOptionalComplement() {
        ValidationResult result = validator.validate(new RegistrationForm("71234567", "1234567890", false, ""));

        assertTrue(result.isValid());
    }

    @Test
    public void acceptsComplementOfTwoLettersOrDigitsWhenPresent() {
        ValidationResult result = validator.validate(new RegistrationForm("71234567", "1234567890", true, "1D"));

        assertTrue(result.isValid());
    }

    @Test
    public void rejectsPhoneLongerThanEightOrWithLetters() {
        assertEquals(FieldErrorCode.INVALID_LENGTH, error(new RegistrationForm("123456789", "1234567890", false, ""), Field.PHONE));
        assertEquals(FieldErrorCode.INVALID_CHARACTERS, error(new RegistrationForm("7123A567", "1234567890", false, ""), Field.PHONE));
        assertEquals(FieldErrorCode.REQUIRED, error(new RegistrationForm("", "1234567890", false, ""), Field.PHONE));
    }

    @Test
    public void rejectsCarnetThatIsNotExactlyTenDigits() {
        assertEquals(FieldErrorCode.INVALID_LENGTH, error(new RegistrationForm("71234567", "123456789", false, ""), Field.CARNET));
        assertEquals(FieldErrorCode.INVALID_CHARACTERS, error(new RegistrationForm("71234567", "123456789A", false, ""), Field.CARNET));
    }

    @Test
    public void rejectsComplementWithSpecialCharactersOrWrongLength() {
        assertEquals(FieldErrorCode.INVALID_CHARACTERS, error(new RegistrationForm("71234567", "1234567890", true, "1@"), Field.COMPLEMENT));
        assertEquals(FieldErrorCode.INVALID_LENGTH, error(new RegistrationForm("71234567", "1234567890", true, "A"), Field.COMPLEMENT));
        assertEquals(FieldErrorCode.REQUIRED, error(new RegistrationForm("71234567", "1234567890", true, ""), Field.COMPLEMENT));
    }

    @Test
    public void stripsDisallowedCharactersWhileTyping() {
        assertEquals("71234567", RegistrationValidator.digitsOnly("71-234.5679", 8));
        assertEquals("1D", RegistrationValidator.alphanumericOnly("1@D!", 2));
    }

    private FieldErrorCode error(RegistrationForm form, Field field) {
        return validator.validate(form).errorFor(field);
    }
}
