package com.bille.domain.model;

public record RegistrationReceipt(String id) {
    public RegistrationReceipt {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El comprobante del servicio necesita un id");
        }
    }
}
