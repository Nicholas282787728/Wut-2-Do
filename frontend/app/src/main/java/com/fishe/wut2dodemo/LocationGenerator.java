package com.fishe.wut2dodemo;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    /**
     *
     */
    public interface LocationUpdate {
        void updateLocation(Location location);
    }

    /**
     * Initialises the necessary components to generate user's location.
     * @param context           The Context that called this constructor.
     * @param locationUpdate    The Activity implementing LocationUpdate that called this constructor.
     */
    public LocationGenerator(Context context, LocationUpdate locationUpdate) {
        Log.i(TAG, "Initialising LocationGenerator.");
        initialiseGoogleApiClient(context);
        createLocationRequest();
        mLocationUpdate = locationUpdate;
        mContext = context;
    }

    /**
     *
     * @param context   The Context that called this method.
     */
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

    /**
     * Automatically called when GoogleApiClient connection is suspended temporarily.
     * It will try to automatically resolve by reconnecting GoogleApiClient if possible.
     * @param cause Reason for disconnection.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("TAG", "GoogleApiClient connection suspended with code: " + cause);
    }

    /**
     * Automatically called when GoogleApiClient fails to connect to Google servers.
     * It will try to automatically resolve by reconnecting GoogleApiClient if possible.
     * @param result    A ConnectionResult that can be used for resolving the error,
     *                  and deciding what sort of error occurred.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
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
     * Automatically called upon GoogleApiClient successful connection.
     * It will begin to request for location updates.
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected.");
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                Log.i(TAG, "Able to detect user's previous location.");
                mLocationUpdate.updateLocation(location);
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException se) {
            Log.i(TAG, "Security Exception caught: " + se.getMessage());
        }
    }

    /**
     * Updates user's location when the user moves around. Automatically called when user' location
     * changes.
     * @param location  User's new location.
     */
    @Override
    public void onLocationChanged(Location location) {
        mLocationUpdate.updateLocation(location);
    }

    /**
     * Reconnects GoogleApiClient and request for location updates.
     */
    public void resumeLocationUpdates() {
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }

        Log.i(TAG, "Resuming GoogleApiClient connection.");
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException se) {
            Log.i(TAG, "Security Exception caught: " + se.getMessage());
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

    public GoogleApiClient getGoogleApiClient() { return mGoogleApiClient; }
}
