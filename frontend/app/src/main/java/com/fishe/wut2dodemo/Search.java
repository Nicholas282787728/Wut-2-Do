package com.fishe.wut2dodemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class Search extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    EditText searchResult;
    String result;

    // location variables
    private GoogleApiClient mGoogleApiClient; // to access Google API
    private Location mLastLocation; // last location of user
    private double mLatitude; // converted to lat and long
    private double mLongitude;
    private LocationRequest mLocationRequest; // set the frequency of getting location request
    private boolean mRequestingLocationUpdates = true; // to constantly receive location updates

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (SaveSharedPreference.getIsLogIn(Search.this).equals("true")){
            getMenuInflater().inflate(R.menu.logout_toobar, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.login_toolbar, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout: {
                SaveSharedPreference.logout(getApplicationContext(),"false");
                finish();
                startActivity(getIntent());
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.userInfo: {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "User Info", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.login:{
               // Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*
    public void settingButton(View viewPop) {

        if(SaveSharedPreference.getIsLogIn(Search.this).equals("true"))
        {
            PopupMenu popup = new PopupMenu(this, viewPop);
            MenuInflater inflaterPop = popup.getMenuInflater();
            inflaterPop.inflate(R.menu.logout, popup.getMenu());

            popup.show();
            popup.setOnMenuItemClickListener(this);

        }
        else  //if not logged in
        {
            PopupMenu popup = new PopupMenu(this, viewPop);
            MenuInflater inflaterPop = popup.getMenuInflater();
            inflaterPop.inflate(R.menu.userinfo, popup.getMenu());

            popup.show();
            popup.setOnMenuItemClickListener(this);
        }

    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:{
                SaveSharedPreference.logout(getApplicationContext(),"false");
                finish();
                startActivity(getIntent());
                return true;
            }
            case R.id.userInfo:{
                Intent i = new Intent(getApplicationContext(), Setting.class);

                startActivity(i);
                return true;
            }
            case R.id.login:{
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);

            }
            default: break;
        }
        return false;
    }
*/
    public void search(View view){

        Log.i("Search",searchResult.getText().toString());
        result = searchResult.getText().toString();
        result = result.replace(" ","+");
        Log.i("Result", result);

        // Check if the user entered username.
        if (TextUtils.isEmpty(result)) {
            searchResult.setError(getString(R.string.enter));
        }

        else{
            searchResult.setError(null);
            Intent i = new Intent(getApplicationContext(), ResultPage.class);
            Bundle extras = new Bundle();
            extras.putString("name",result);
            extras.putString("code","search");
            extras.putDouble("lat",mLatitude);
            extras.putDouble("lng",mLongitude);
            i.putExtras(extras);
            startActivityForResult(i, 1);
        }

//        startActivity(i);
//        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==1 && resultCode == 1)
        {
            //
            startActivity(getIntent());
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
        searchResult = (EditText)findViewById(R.id.editText);

        // in-case function
        searchResult.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            // if user clicks login_button or clicks enter after keying in password, it will log in.
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.imageButton || id == EditorInfo.IME_ACTION_DONE) {
                    search(textView);
                    return true;
                }
                return false;
            }
        });


    }
    // necessary for MainActivity to implement ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("Connection lost?", "Oh no");
    }

    // necessary for MainActivity to implement OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("Connection failed?", "Oh no");
    }

    // set the accuracy and interval of results
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // connects to Google API
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    // Upon connection
    @Override
    public void onConnected(Bundle connectionHint) {
        // check for permissions. necessary before calling the below code
        // TODO: Request permissions. Actually no permission also ok, the app works fine without it
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                    , 10);
        } else {
            // first generation of user's location. have to put here to prevent error from above code.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();
                Log.i("lat", String.valueOf(mLastLocation.getLatitude()));
                Log.i("lng", String.valueOf(mLastLocation.getLongitude()));

            }

            // begin to track user's location and update
            if (mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
    }

    // start tracking user's location
    protected void startLocationUpdates() {
        // check for permissions. necessary before calling the below code.
        // TODO: Request permissions part 2
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                    , 10);
        }

        // request location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    // when user moves around, update the user's location
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateUI();
    }

    // helper method: prints out the user's lat long.
    private void updateUI() {
        mLatitude = mLastLocation.getLatitude();
        mLongitude = mLastLocation.getLongitude();

    }

    // if user opens other app, stop location updates to save battery
    @Override
    protected void onPause() {
        super.onPause();
        //    stopLocationUpdates();
    }

    // helper method called by onPause()
 /*   protected void stopLocationUpdates() {
        // stop location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }*/

    // when user returns to this app, resume location updates
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    // disconnects from Google API
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}
