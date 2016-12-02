package com.fishe.wut2dodemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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

    public static final String FINE_LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String SUBSEQUENT_REQUEST_FOR_PERMISSION = "Please give permission " +
            " to access your location to enjoy the full functionalities of our awesome application.";

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isPermissionGranted()) {
            Log.i(this.getLocalClassName(), "Permissions are granted.");
            onPermissionsGranted(requestCode);
        } else {
            Snackbar snackbar =  Snackbar.make(findViewById(android.R.id.content),
                    SUBSEQUENT_REQUEST_FOR_PERMISSION, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            TextView textView= (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setMaxLines(3);
            snackbar.show();
        }
    }

    protected boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request location permission from user.
     * @param requestCode   Identifier for the request that was made.
     */
    protected void requestAppPermissions(final int requestCode) {
        if (isPermissionGranted()) {
            Log.i(this.getLocalClassName(), "Permissions already granted.");
            onPermissionsGranted(requestCode);
        } else {
            //if (ActivityCompat.shouldShowRequestPermissionRationale(this,
            //        android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (false) {
                Log.i(this.getLocalClassName(), "Showing request permission rationale.");
                Snackbar.make(findViewById(android.R.id.content), "Please give permissions.",
                        Snackbar.LENGTH_LONG).setAction("GRANT",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(RuntimePermissionsActivity.this,
                                        new String[]{FINE_LOCATION_PERMISSION}, requestCode);
                            }
                        }).show();
            } else {
                Log.i(this.getLocalClassName(), "Not showing requesting permission rationale.");
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
        }
    }

    /**
     * Actions to take after permissions have been successfully granted.
     * @param requestCode   Identifier for the request that was made.
     */
    public abstract void onPermissionsGranted(final int requestCode);
}
