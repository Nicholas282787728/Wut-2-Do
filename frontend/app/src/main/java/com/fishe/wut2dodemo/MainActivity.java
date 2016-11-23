package com.fishe.wut2dodemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // location variables
    private GoogleApiClient mGoogleApiClient; // to access Google API
    private Location mLastLocation; // last location of user
    private double mLatitude; // converted to lat and long
    private double mLongitude;
    private LocationRequest mLocationRequest; // set the frequency of getting location request
    private boolean mRequestingLocationUpdates = true; // to constantly receive location updates
    // variables for tutorial
    private ImageButton search, genre, random, login;
    final String PREF_NAME = "MyPrefs";

   // keytool -exportcert -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore | openssl sha1 -binary | openssl base64

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (SaveSharedPreference.getIsLogIn(MainActivity.this).equals("true")){
            getMenuInflater().inflate(R.menu.logout_toobar, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.login_toolbar, menu);
        }
        getMenuInflater().inflate(R.menu.extras,menu);
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
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);
                return true;
            }
            case R.id.credits:{
                Intent i = new Intent(getApplicationContext(), About.class);
                startActivity(i);
                return true;
            }
            case R.id.tutorial:{
                doTutorial();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void search(View view){

        Intent i = new Intent(getApplicationContext(), Search.class);

        startActivity(i);
    }
    public void category(View view){

        Intent i = new Intent(getApplicationContext(), Genre.class);

        startActivity(i);
    }

    public void random(View view){

        Intent i = new Intent(getApplicationContext(), ResultPage.class);
        Bundle extras = new Bundle();
        extras.putString("code", "random");
        extras.putDouble("lat",mLatitude);
        extras.putDouble("lng",mLongitude);
        i.putExtras(extras);

        startActivity(i);
    }

    public void login(View view){

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        //Intent i = new Intent(getApplicationContext(), QuestionUser.class);

        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
   //     getSupportActionBar().setLogo(R.drawable.app);
   //     getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_main);

        search = (ImageButton)findViewById(R.id.search);
        genre = (ImageButton)findViewById(R.id.genre);
        random = (ImageButton)findViewById(R.id.random);
        login = (ImageButton)findViewById(R.id.login);

        SharedPreferences settings = getSharedPreferences(PREF_NAME,0);

        if(settings.getBoolean("first_time",true)){
            doTutorial();
            settings.edit().putBoolean("first_time",false).commit();
        }

        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
        //if not logged in
        if(SaveSharedPreference.getIsLogIn(MainActivity.this).equals("true"))
        {
            Toast.makeText(getApplicationContext(), "Welcome Back " + SaveSharedPreference.getUserName(MainActivity.this),Toast.LENGTH_SHORT).show();
            Log.i("User", "Logged in");
        }
        else    //if logged in show welcome username
        {
            QuestionSharedPreference.setDefault(getApplicationContext());
            Log.i("User", "Logged out");
        }

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

    private void doTutorial(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(0);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);

        sequence.addSequenceItem(search, "Search",
                "Enter names of towns or buildings to find entertainment near the location", "Got it!");
        sequence.addSequenceItem(genre, "Genre",
                "Find activities to do near you based on the activities' genre", "Got it!");
        sequence.addSequenceItem(random, "What's Fun?",
                "Feeling lucky? Click to generate a random activity near you", "Got it!");
        sequence.addSequenceItem(login, "Log In",
                "Create an account or log in", "Got it!");

        sequence.start();
    }
}
