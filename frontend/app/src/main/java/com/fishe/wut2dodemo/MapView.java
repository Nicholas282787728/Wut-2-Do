package com.fishe.wut2dodemo;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapView extends LocationPermissionActivity implements OnMapReadyCallback, LocationGenerator.LocationUpdate {

    public static final String TAG = MapView.class.getSimpleName();
    private GoogleMap mMap;
    ArrayList<String> itemList;
    ArrayList<String> latlngList;
    private LatLng userCoordinates;

    @Override
    public void updateLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        userCoordinates = new LatLng(latitude, longitude);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        requestAppPermissions(9998);
        requestTurnOnGps();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        latlngList = new ArrayList<String>();

        Intent i = getIntent();
        latlngList = i.getStringArrayListExtra("latlng");
        itemList = i.getStringArrayListExtra("location");
        //TODO: Multithreading
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                locationGenerator.lockThreadUntilConnectionIsUp();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addMarker(new MarkerOptions().position(userCoordinates).
                                title("User location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userCoordinates));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCoordinates, 16));
                    }
                });
            }
        };
        new Thread(runnable).start();

        for(int j=0; j<latlngList.size();j++){
            String[] address = itemList.get(j).split("\n");

            String[] result = latlngList.get(j).split(",");
            LatLng placeOfInterest = new LatLng(Double.parseDouble(result[0]),Double.parseDouble(result[1]));
            mMap.addMarker(new MarkerOptions().position(placeOfInterest).title(address[0]).snippet(address[1]));

//            mMap.addMarker(new MarkerOptions().position(placeOfInterest).title(address[0] + "\r\n" + address[1]));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(placeOfInterest));

        }


    }

    @Override
    protected void onResume() {
        Log.i(TAG, "Resuming activity.");
        if (locationGenerator != null) {
            locationGenerator.resumeLocationUpdates();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "Pausing activity.");
        if (locationGenerator != null) {
            locationGenerator.pauseLocationUpdates();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "Stopping activity.");
        if (locationGenerator != null) {
            locationGenerator.stopLocationUpdates();
        }
        super.onStop();
    }
}

