package com.fishe.wut2dodemo;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.LocationServices;

public abstract class Location extends AppCompatActivity {
    protected GoogleLocationApi googleLocationApi = null;

    @Override
    protected void onPause() {
        super.onPause();
        if (googleLocationApi != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleLocationApi.getGoogleApiClient(), googleLocationApi);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleLocationApi != null && googleLocationApi.getGoogleApiClient().isConnected()) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleLocationApi.getGoogleApiClient(),
                        googleLocationApi.getLocationRequest(), googleLocationApi);
            } catch (SecurityException se) {
                assert false;
            }
        }
    }

    @Override
    protected void onStop() {
        if (googleLocationApi != null) {
            googleLocationApi.getGoogleApiClient().disconnect();
        }
        super.onStop();
    }
}
