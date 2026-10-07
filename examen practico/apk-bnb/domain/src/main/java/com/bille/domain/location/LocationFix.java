package com.bille.domain.location;

public record LocationFix(boolean available, double latitude, double longitude) {

    public static LocationFix unavailable() {
        return new LocationFix(false, 0d, 0d);
    }
}
