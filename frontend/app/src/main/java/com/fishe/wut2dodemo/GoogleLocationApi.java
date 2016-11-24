package com.fishe.wut2dodemo;

import android.app.Activity;
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

/*
 * GoogleLocationApi generates the user's location by using Google LocationMethods API.
 */
public class GoogleLocationApi implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final int DEFAULT_INTERVAL = 10000;
    public static final int DEFAULT_FASTEST_INTERVAL = 5000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private UserCoordinates userCoordinates;
    private static GoogleLocationApi theOneInstance = null;

    private static Activity callbackActivity;

    /*
     * Singleton
     * @param activity  The activity that calls this method.
     * @return          Instance of GoogleLocationApi.
     */
    public static GoogleLocationApi initialise(Activity activity) {
        callbackActivity = activity;
        if (theOneInstance == null)
            theOneInstance = new GoogleLocationApi(activity);
        return theOneInstance;
    }

    public static GoogleLocationApi getInstance() {
        if (theOneInstance == null && callbackActivity != null)
            theOneInstance = new GoogleLocationApi(callbackActivity);
        return theOneInstance;
    }

    private GoogleLocationApi(Activity activity) {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("Connection lost?", "Oh no");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("Connection failed?", "Oh no");
    }

    /*
     * Sets the accuracy and interval of results to default values
     */
    private void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(DEFAULT_INTERVAL);
            mLocationRequest.setFastestInterval(DEFAULT_FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    /*
     * Requests locations and internet permissions from user, and generates user location.
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (callbackActivity == null)
            return;
        // TODO: Request permissions. Actually no permission also ok, the app works fine without it
        if (arePermissionsGranted()) {
            try {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    userCoordinates = UserCoordinates.getInstance(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Log.i("lat: ", String.valueOf(mLastLocation.getLatitude()));
                    Log.i("lng: ", String.valueOf(mLastLocation.getLongitude()));
                }

                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            } catch (SecurityException se) {
                assert false;
            }
        } else {
            callbackActivity.requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.INTERNET}, 10);
        }
    }

    /*
     * Updates user's location when the user moves around.
     */
    @Override
    public void onLocationChanged(Location location) {
        userCoordinates.updateCoordinates(location.getLatitude(), location.getLongitude());
    }

    private boolean arePermissionsGranted() {
        return ContextCompat.checkSelfPermission(callbackActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(callbackActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void pauseLocationUpdates() {
        if (theOneInstance != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    theOneInstance.getGoogleApiClient(), theOneInstance);
        }
    }

    public static void resumeLocationUpdates() {
        if (theOneInstance != null && theOneInstance.getGoogleApiClient().isConnected()) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        theOneInstance.getGoogleApiClient(),
                        theOneInstance.getLocationRequest(), theOneInstance);
            } catch (SecurityException se) {
                assert false;
            }
        }
    }

    public static void stopLocationUpdates() {
        if (theOneInstance != null) {
            theOneInstance.getGoogleApiClient().disconnect();
        }
    }

    public GoogleApiClient getGoogleApiClient() { return mGoogleApiClient; }
    public UserCoordinates getUserCoordinates() {
        return userCoordinates;
    }
    public LocationRequest getLocationRequest() { return mLocationRequest; }
}
