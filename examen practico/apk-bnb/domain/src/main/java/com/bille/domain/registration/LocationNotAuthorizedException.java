package com.bille.domain.registration;

public class LocationNotAuthorizedException extends RuntimeException {
    public LocationNotAuthorizedException() {
        super("El servicio solo se consume con el permiso de ubicación habilitado");
    }
}
