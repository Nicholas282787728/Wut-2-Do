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

    private static final String TAG = MapView.class.getSimpleName();

    private static final String USER_LOCATION_STRING = "User Location";
    private static final String NEW_LINE = "\n";
    private static final String COMMA = ",";
    public static final int LONGITUDE_INDEX = 1;
    private static final int LATITUDE_INDEX = 0;
    private static final int NAME_INDEX = 0;
    private static final int ADDRESS_INDEX = 1;
    private static final int ARRAY_BASE_INDEX = 0;
    public static final int STANDARD_MAP_ZOOM_IN_LEVEL = 16;
    public static final String LATLNG_INTENT_STRING = "latlng";
    public static final String LOCATION_INTENT_STRING = "location";

    private GoogleMap mMap;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationGenerator.onCallerActivityResult(requestCode, resultCode);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        final ArrayList<String> locationDetailsList = intent.getStringArrayListExtra(LATLNG_INTENT_STRING);
        final ArrayList<String> coordinatesList = intent.getStringArrayListExtra(LOCATION_INTENT_STRING);

        new Thread(new Runnable() {
            @Override
            public void run() {
                locationGenerator.lockThreadUntilConnectionIsUp();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addUserLocationMarker();
                        addPlacesLocationMarkers(locationDetailsList, coordinatesList);
                        moveToUserLocationMarker();
                    }
                });
            }
        }).start();
    }

    private void addUserLocationMarker() {
        mMap.addMarker(new MarkerOptions().position(userCoordinates).
                title(USER_LOCATION_STRING).icon(BitmapDescriptorFactory.
                defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    private void moveToUserLocationMarker() {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userCoordinates));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCoordinates, STANDARD_MAP_ZOOM_IN_LEVEL));
    }

    private void addPlacesLocationMarkers(ArrayList<String> locationDetailsList,
                                          ArrayList<String> coordinatesList) {
        assert coordinatesList.size() == locationDetailsList.size();

        for (int i = ARRAY_BASE_INDEX; i < coordinatesList.size(); i++){
            String[] detailsOfLocation = locationDetailsList.get(i).split(NEW_LINE);
            String[] coordinateValues = coordinatesList.get(i).split(COMMA);

            LatLng coordinatesOfLocation = new LatLng(Double.parseDouble(coordinateValues[LATITUDE_INDEX]),
                    Double.parseDouble(coordinateValues[LONGITUDE_INDEX]));

            mMap.addMarker(new MarkerOptions().position(coordinatesOfLocation).
                    title(detailsOfLocation[NAME_INDEX]).snippet(detailsOfLocation[ADDRESS_INDEX]));
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

