package com.fishe.wut2dodemo;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * LocationGenerator generates the user's location by using Google Location API.
 */
public class LocationGenerator implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int DEFAULT_INTERVAL = 10 * 1000;
    private static final int DEFAULT_FASTEST_INTERVAL = 5 * 1000;
    private static final String TAG = LocationGenerator.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9999;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationUpdate mLocationUpdate;
    private Context mContext;
    private boolean isFunctioning = false;

    public interface LocationUpdate {
        void updateLocation(Location location);
    }

    /**
     * @param context  The context that calls this method.
     */
    public LocationGenerator(Context context, LocationUpdate locationUpdate) {
        initialiseGoogleApiClient(context);
        createLocationRequest();
        mLocationUpdate = locationUpdate;
        mContext = context;
    }

    private void initialiseGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Sets the accuracy of generating user location and the interval of updating
     * the location of user location to default values.
     */
    private void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(DEFAULT_INTERVAL);
            mLocationRequest.setFastestInterval(DEFAULT_FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("TAG", "GoogleApiClient connection suspended with code: " + cause);
        isFunctioning = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("TAG", "GoogleApiClient failed to connect.");
        if (result.hasResolution() && mContext instanceof Activity) {
            try {
                Activity activity = (Activity) mContext;
                result.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.i(TAG, e.getMessage());
            }
        } else {
            Log.i(TAG, "Location services connection failed with code: " + result.getErrorCode());
        }
    }

    /**
     * Requests locations and internet permissions from user, and generates user location.
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected.");
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                mLocationUpdate.updateLocation(location);
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            isFunctioning = true;
        } catch (SecurityException se) {
            Log.i(TAG, "Security Exception caught.");
            isFunctioning = false;
        }
    }

    private boolean arePermissionsGranted() {
        // TODO: Request permissions. Actually no permission also ok, the app works fine without it
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            activity.requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.INTERNET}, 10);
    }/*
    Log.i("lat: ", String.valueOf(mLastLocation.getLatitude()));
    Log.i("lng: ", String.valueOf(mLastLocation.getLongitude()));*/

    /**
     * Updates user's location when the user moves around.
     */
    @Override
    public void onLocationChanged(Location location) {
        mLocationUpdate.updateLocation(location);
    }

    public void resumeLocationUpdates() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        Log.i(TAG, "Resuming GoogleApiClient connection.");
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException se) {
            Log.i(TAG, "Security Exception caught.");
            isFunctioning = false;
        }
    }

    public void pauseLocationUpdates() {
        Log.i(TAG, "Pausing GoogleApiClient connection.");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void stopLocationUpdates() {
        Log.i(TAG, "Stopping GoogleApiClient connection.");
        mGoogleApiClient.disconnect();
    }

    public boolean getFunctioning() {
        return isFunctioning;
    }
}
