package com.fishe.wut2dodemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Category extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
    ListView listView;
    ArrayList<String> itemList;
    String category;
    TextView nameCat;
    ProgressDialog dialog;

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
        if (SaveSharedPreference.getIsLogIn(Category.this).equals("true")){
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
          //      Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            try {
                Log.i("Error ", "json testing");
                if(result==null){
                    finish();
                    startActivity(getIntent());
                }
                JSONArray array = new JSONArray(result);

                itemList = new ArrayList<String>();

                for (int i = 0; i < array.length(); i++) {

                    JSONObject jsonPart = array.getJSONObject(i);

                    itemList.add(jsonPart.getString("name"));

                }

                final ArrayList<String> categoryList = itemList;
                listView = (ListView) findViewById(R.id.listViewCategory);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, categoryList);
                listView.setAdapter(arrayAdapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(), ResultPage.class);
                        QuestionSharedPreference.increasePref(getBaseContext(),categoryList.get(position));
                        Log.i("Chosen to increase ", categoryList.get(position));
                        Bundle extras = new Bundle();
                        extras.putString("name", categoryList.get(position));
                        extras.putString("code", "category");
                        extras.putDouble("lat",mLatitude);
                        extras.putDouble("lng",mLongitude);

                        i.putExtras(extras);
                        startActivity(i);

                       // Toast.makeText(getApplicationContext(), "Category: " + categoryList.get(position), Toast.LENGTH_SHORT).show();
                        Log.i("Category chosen", categoryList.get(position));
                    }
                });

            } catch (JSONException e) {
                Log.i("Error ", "json failed");
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Downloading information ");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                nameCat = (TextView) findViewById(R.id.categoryPg);
                category = "";
                Intent receive = getIntent();
                category = receive.getStringExtra("name");

                Log.i("Name", category);
                //display the name as the title
                nameCat.setText(receive.getStringExtra("name"));

                String url = "http://orbital_wut_2_do.net16.net/copy/output/show_categories.php?genre=" + category;
                Log.i("URL", url);

                DownloadTask task = new DownloadTask();

                try {
                    task.execute(url).get();

                } catch (InterruptedException e) {
                    Log.i("Interrupted", category);
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Log.i("Execution", category);
                }

                dialog.dismiss();
            }
        };

        new Thread(runnable).start();
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

