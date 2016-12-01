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

/**
 * GoogleLocationApi generates the user's location by using Google Location API.
 * Singleton pattern applied.
 */
public class GoogleLocationApi implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int DEFAULT_INTERVAL = 10000;
    public static final int DEFAULT_FASTEST_INTERVAL = 5000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private UserCoordinates userCoordinates;
    private static GoogleLocationApi theGoogleLocationApi = null;

    /**
     *
     * @param activity  The activity that calls this method.
     * @return          Instance of GoogleLocationApi.
     */
    public static GoogleLocationApi getInstance() {
        if (theGoogleLocationApi == null)
            theGoogleLocationApi = new GoogleLocationApi();
        return theGoogleLocationApi;
    }

    /**
     *
     * @param activity  The activity that calls this method.
     */
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
    public void onConnectionSuspended(int cause) throws Exception {
        //throw
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("Connection failed?", "Oh no");
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

    /**
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

    /**
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
        if (theGoogleLocationApi != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    theGoogleLocationApi.getGoogleApiClient(), theGoogleLocationApi);
        }
    }

    public static void resumeLocationUpdates() {
        if (theGoogleLocationApi != null && theGoogleLocationApi.getGoogleApiClient().isConnected()) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        theGoogleLocationApi.getGoogleApiClient(),
                        theGoogleLocationApi.getLocationRequest(), theGoogleLocationApi);
            } catch (SecurityException se) {
                assert false;
            }
        }
    }

    public static void stopLocationUpdates() {
        if (theGoogleLocationApi != null) {
            theGoogleLocationApi.getGoogleApiClient().disconnect();
        }
    }

    public GoogleApiClient getGoogleApiClient() { return mGoogleApiClient; }
    public UserCoordinates getUserCoordinates() {
        return userCoordinates;
    }
    public LocationRequest getLocationRequest() { return mLocationRequest; }
}
