package com.bille.data.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;

import com.bille.domain.location.LocationFix;

import androidx.core.content.ContextCompat;

import com.bille.domain.location.LocationGateway;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class AndroidLocationGateway implements LocationGateway {

    private final Context context;

    @Inject
    public AndroidLocationGateway(@ApplicationContext Context context) {
        this.context = context;
    }

    @Override
    public boolean hasLocationPermission() {
        return granted(Manifest.permission.ACCESS_FINE_LOCATION)
                || granted(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isLocationEnabled();
    }

    @Override
    @SuppressLint("MissingPermission")
    public LocationFix readFix() {
        if (!hasLocationPermission()) {
            return LocationFix.unavailable();
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return LocationFix.unavailable();
        }
        Location location = lastKnown(locationManager, LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            location = lastKnown(locationManager, LocationManager.GPS_PROVIDER);
        }
        if (location == null) {
            return LocationFix.unavailable();
        }
        return new LocationFix(true, location.getLatitude(), location.getLongitude());
    }

    @SuppressLint("MissingPermission")
    private Location lastKnown(LocationManager locationManager, String provider) {
        try {
            if (!locationManager.isProviderEnabled(provider)) {
                return null;
            }
            return locationManager.getLastKnownLocation(provider);
        } catch (IllegalArgumentException | SecurityException exception) {
            return null;
        }
    }

    private boolean granted(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
