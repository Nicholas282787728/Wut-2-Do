package com.fishe.wut2dodemo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fishe.wut2dodemo.ui.messages.PopupMessage;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Abstract parent class containing the methods required to request location permission from user.
 * Activities that require location permission can inherit from this class.
 */
public abstract class LocationPermissionActivity extends AppCompatActivity
        implements LocationGenerator.LocationUpdate {

    private static final String FINE_LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;

    private static final String PERMISSION_GRANTED_MESSAGE = "Yay you can now enjoy the full " +
            "functionalities of our awesome application. ˆˆ";
    private static final String SUBSEQUENT_REQUEST_FOR_PERMISSION_MESSAGE = "Please give permission "
            + "to access your location to enjoy the full functionalities of our awesome application. :)";
    private static final String STOP_REQUEST_FOR_PERMISSION_MESSAGE = "You will not be able to "
            + "enjoy the full functionalities of our awesome application :(.";
    private static final String GRANT_PERMISSION_STRING = "GRANT PERMISSION";

    private static final int THREE = 3;
    private static final int LOCATION_SERVICES_NOT_GRANTED_RESOLUTION_REQUEST = 1000;

    protected LocationGenerator locationGenerator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationGenerator = new LocationGenerator(this, this);
    }

    /**
     * Request location permission from user.
     * @param requestCode   Identifier for the request that was made.
     */
    protected void requestAppPermissions(final int requestCode) {
        if (!isPermissionGranted()) {
            Log.i(getLocalClassName(), "Requesting for permission for code: " + requestCode);
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
        } else {
            Log.i(getLocalClassName(), "Permissions already granted for code: " + requestCode);
        }
    }

    protected boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Runs showNormalMessage if permissions are granted. Else, shows a message to the user
     * to encourage user to grant permissions.
     * @param requestCode   Identifier for the request that was made.
     * @param permissions   The requested permissions.
     * @param grantResults  Results for the corresponding permissions requested for.
     *                      Only takes on 2 values: PERMISSION_GRANTED or PERMISSION_DENIED.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isPermissionGranted()) {
            Log.i(getLocalClassName(), "Permission is granted with code:" + requestCode);
            showPermissionGrantedMessage();
        } else {
            Log.i(getLocalClassName(), "Permission is not granted with code: " + requestCode);
            if (canPromptUserAgain()) {
                Log.i(getLocalClassName(), "Showing request permission rationale and reprompting user.");
                showRepromptMessage(requestCode);
            } else {
                Log.i(getLocalClassName(), "User has selected \"Never ask again\" option.");
                showPermissionPermanentlyNotGrantedMessage();
            }
        }
    }

    private void showRepromptMessage(int requestCode) {
        new PopupMessage(findViewById(android.R.id.content), SUBSEQUENT_REQUEST_FOR_PERMISSION_MESSAGE)
                .setMaxLines(THREE).setSnackbarAction(this, FINE_LOCATION_PERMISSION, GRANT_PERMISSION_STRING, requestCode)
                .showMessage();
        /*popupMessage.setMaxLines(THREE);
        popupMessage.setSnackbarAction(this, FINE_LOCATION_PERMISSION, GRANT_PERMISSION_STRING, requestCode);
        popupMessage.showMessage();*/
    }

    private void showPermissionPermanentlyNotGrantedMessage() {
        new PopupMessage(findViewById(android.R.id.content), STOP_REQUEST_FOR_PERMISSION_MESSAGE)
                .setMaxLines(THREE).showMessage();
        /*PopupMessage popupMessage = new PopupMessage(findViewById(android.R.id.content),
                STOP_REQUEST_FOR_PERMISSION_MESSAGE);
        popupMessage.setMaxLines(THREE);
        popupMessage.showMessage();*/
    }

    private void showPermissionGrantedMessage() {
        new PopupMessage(findViewById(android.R.id.content), PERMISSION_GRANTED_MESSAGE)
                .showMessage();
        /*new PopupMessage(findViewById(android.R.id.content),
                PERMISSION_GRANTED_MESSAGE);
        popupMessage.showMessage();*/
    }

    /**
     * Checks if application has rights to prompt user again to grant permission request.
     * @return  True if user did not select "Never ask again" option when rejecting permission
     *          request.
     */
    private boolean canPromptUserAgain() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Requests permission to turn on location services for user if it is currently turned off.
     */
    protected void requestTurnOnGps() {
        Log.i(getLocalClassName(), "Requesting for permission to turn on GPS");
        PendingResult<LocationSettingsResult> result = initialiseResult();
        setCallback(result);
    }

    /**
     * Checks whether location services is currently turned on.
     * @return  Pending result on whether location services is currently turned on.
     */
    private PendingResult<LocationSettingsResult> initialiseResult() {
        assert locationGenerator != null;

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationGenerator.getLocationRequest())
                .setAlwaysShow(true); // removes the option to select "Never"

        return LocationServices.SettingsApi.checkLocationSettings(
                locationGenerator.getGoogleApiClient(), builder.build());
    }

    /**
     * Check user's current location settings status.
     * @param result    Pending result on whether location services is currently turned on.
     */
    private void setCallback(PendingResult<LocationSettingsResult> result) {
        assert result != null;

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(getLocalClassName(), "Location settings granted.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(getLocalClassName(), "Location settings not granted.");
                        tryToResolve(status);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(getLocalClassName(), "Location settings unable to be changed.");
                        break;
                    default:
                        Log.i(getLocalClassName(), "Location settings went wrong.");
                }
            }

            /**
             * Requests permission to turn on location services for user.
             * @param status    Status on whether it is possible to request permission to turn on
             *                  location services for user.
             */
            private void tryToResolve(Status status) {
                assert status != null;

                try {
                    if (status.hasResolution()) {
                        status.startResolutionForResult(
                                LocationPermissionActivity.this,
                                LOCATION_SERVICES_NOT_GRANTED_RESOLUTION_REQUEST);
                    } else {
                        Log.i(getLocalClassName(), "Location settings unable to be resolved.");
                    }
                } catch (IntentSender.SendIntentException e) {
                    Log.i(getLocalClassName(), e.getMessage());
                }
            }
        });
    }

    /**
     * Called after trying to resolve location settings not granted.
     * @param requestCode   Identifier for the request that was made.
     * @param resultCode    Integer value of result for the activity.
     * @param data          The intent that called the activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOCATION_SERVICES_NOT_GRANTED_RESOLUTION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(getLocalClassName(), "Location settings now granted.");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(getLocalClassName(), "Location settings still not granted.");
                        break;
                    default:
                        Log.i(getLocalClassName(), "Location settings resolution went wrong.");
                        break;
                }
                break;
        }
    }
}
