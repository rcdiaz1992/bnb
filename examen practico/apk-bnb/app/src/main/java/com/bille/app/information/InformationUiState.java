package com.bille.app.information;

import com.bille.domain.validation.Field;
import com.bille.domain.validation.FieldErrorCode;

import java.util.Map;

public record InformationUiState(
        String phone,
        String carnet,
        String complement,
        boolean hasComplement,
        Map<Field, FieldErrorCode> errors,
        boolean canContinue,
        boolean submitting
) {
}
