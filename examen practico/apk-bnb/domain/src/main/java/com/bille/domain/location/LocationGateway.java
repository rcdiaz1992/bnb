package com.bille.domain.location;

public interface LocationGateway {
    boolean hasLocationPermission();

    boolean isLocationEnabled();

    LocationFix readFix();
}
