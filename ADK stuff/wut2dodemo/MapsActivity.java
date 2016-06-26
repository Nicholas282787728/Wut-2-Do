package com.fishe.wut2dodemo;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lat=0,lng=0;
    String address;
    String postal;
    String latlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent receive = getIntent();
        latlng = receive.getStringExtra("latlng");
        String[] result = latlng.split(",");

/*
        address = receive.getStringExtra("name");
        String[] result = address.split("\n");
        postal = findPostal(result[1]);
*/
/*
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            List<Address> listAddress = geocoder.getFromLocationName(postal,1);

            if(listAddress!=null && listAddress.size() > 0){
                Log.i("PlaceInfo", listAddress.get(0).toString());
                android.location.Address address = listAddress.get(0);
                lat =(address.getLatitude());
                lng =(address.getLongitude());
                Log.i("Latitude", String.valueOf(lat));
                Log.i("longitude", String.valueOf(lng));

            }
            else{
                Log.i("PlaceInfo", "No address found");
            }

        } catch (IOException e) {
            Log.i("PlaceInfo", "Error in address found");

            e.printStackTrace();
        }
*/
        LatLng placeOfInterest = new LatLng(Double.parseDouble(result[0]),Double.parseDouble(result[1]));
        mMap.addMarker(new MarkerOptions().position(placeOfInterest).title("Location to visit"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(placeOfInterest));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeOfInterest,16));
    }




    public String findPostal(String str){
        Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9][0-9][0-9]");
        Matcher matcher = pattern.matcher(str);

        if(matcher.find())
            return matcher.group();

        else
            return "Wrong";

    }

}
