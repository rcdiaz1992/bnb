package com.bille.app.information;

public record LocationAccess(boolean permissionGranted, boolean locationEnabled) {

    public boolean isReady() {
        return permissionGranted && locationEnabled;
    }
}
