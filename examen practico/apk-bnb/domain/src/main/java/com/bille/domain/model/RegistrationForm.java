package com.bille.domain.model;

public record RegistrationForm(
        String phone,
        String carnet,
        boolean hasComplement,
        String complement
) {
    public RegistrationForm {
        if (phone == null || carnet == null || complement == null) {
            throw new IllegalArgumentException("Los campos del formulario no pueden ser nulos");
        }
    }
}
