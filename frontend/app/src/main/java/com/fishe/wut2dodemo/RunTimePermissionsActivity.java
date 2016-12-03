package com.fishe.wut2dodemo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.support.design.widget.Snackbar;
import android.widget.TextView;

/**
 * Abstract parent class containing the methods required to request location permission from user.
 * Activities that require location permission can inherit from this class.
 */
public abstract class RuntimePermissionsActivity extends AppCompatActivity {

    private static final String FINE_LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;

    private static final String PERMISSION_GRANTED_MESSAGE = "Yay you can now enjoy the full " +
            "functionalities of our awesome application. ˆˆ";
    private static final String SUBSEQUENT_REQUEST_FOR_PERMISSION_MESSAGE = "Please give permission "
            + "to access your location to enjoy the full functionalities of our awesome application. :)";
    private static final String STOP_REQUEST_FOR_PERMISSION_MESSAGE = "You will not be able to "
            + "enjoy the full functionalities of our awesome application :(.";
    private static final String GRANT_PERMISSION_STRING = "GRANT PERMISSION";

    private static final int THREE = 3;
    private static final int PERMISSION_INDEX = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Runs onPermissionsGranted if permissions are granted. Else, shows a message to the user
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
        if (grantResults[PERMISSION_INDEX] == PackageManager.PERMISSION_GRANTED) {

            Log.i(this.getLocalClassName(), "Permission is granted with code:" + requestCode);
            onPermissionsGranted(requestCode);
        } else if (grantResults[PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED) {

            Log.i(this.getLocalClassName(), "Permission is not granted with code: " + requestCode);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                Log.i(this.getLocalClassName(), "Showing request permission rationale.");
                onPermissionsNotGranted(requestCode);
            } else {

                Log.i(this.getLocalClassName(), "User has selected \"Never ask again\" option.");
                onPermissionsPermanentlyNotGranted();
            }

        }
    }

    /**
     * When user chooses to grant permission request, shows the user
     * a message informing him that he / she has successfully granted permission.
     * @param requestCode   Identifier for the request that was made.
     */
    private void onPermissionsGranted(final int requestCode) {
        Snackbar.make(findViewById(android.R.id.content), PERMISSION_GRANTED_MESSAGE, Snackbar.LENGTH_LONG).show();
    }

    /**
     * When user chooses not to grant permission request, shows the user
     * a message informing that he / she is unable to enjoy full functionalities of the application
     * and prompts the user to grant permission request.
     * @param requestCode   Identifier for the request that was made.
     */
    private void onPermissionsNotGranted(final int requestCode) {
        Snackbar snackbar =  Snackbar.make(findViewById(android.R.id.content),
                SUBSEQUENT_REQUEST_FOR_PERMISSION_MESSAGE, Snackbar.LENGTH_LONG);
        setSnackbarAction(snackbar, requestCode);
        setSnackbarMaxLines(snackbar, THREE);
        snackbar.show();
    }

    /**
     * Sets a clickable action to the snackbar, allowing the user to grant permission on click.
     * @param snackbar      The snackbar to add the clickable action to.
     * @param requestCode   Identifier for the request that was made.
     */
    private void setSnackbarAction(Snackbar snackbar, final int requestCode) {
        snackbar.setAction(GRANT_PERMISSION_STRING, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(RuntimePermissionsActivity.this,
                        new String[]{FINE_LOCATION_PERMISSION}, requestCode);
            }
        });
    }

    /**
     * Sets the maximum number of lines shown in snackbar to prevent truncation.
     */
    private void setSnackbarMaxLines(Snackbar snackbar, int maxLines) {
        View snackbarView = snackbar.getView();
        TextView textView= (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(maxLines);
    }

    /**
     * When user checks box "Never ask again" for the permission request, shows the user
     * a message informing that he / she is unable to enjoy full functionalities of the application.
     */
    private void onPermissionsPermanentlyNotGranted() {
        Snackbar snackbar =  Snackbar.make(findViewById(android.R.id.content),
                STOP_REQUEST_FOR_PERMISSION_MESSAGE, Snackbar.LENGTH_LONG);
        setSnackbarMaxLines(snackbar, THREE);
        snackbar.show();
    }

    /**
     * Request location permission from user.
     * @param requestCode   Identifier for the request that was made.
     */
    protected void requestAppPermissions(final int requestCode) {
        if (isPermissionGranted()) {
            Log.i(this.getLocalClassName(), "Permissions already granted for code: " + requestCode);
            onPermissionsGranted(requestCode);
        } else {
            Log.i(this.getLocalClassName(), "Requesting for permission for code: " + requestCode);
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
        }
    }

    protected boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
