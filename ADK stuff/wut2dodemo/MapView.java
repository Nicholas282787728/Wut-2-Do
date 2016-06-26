package com.fishe.wut2dodemo;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<String> itemList;
    ArrayList<String> latlngList;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        latlngList = new ArrayList<String>();
        double lat = 1.348796;
        double lng = 103.749428;

        Intent i = getIntent();
        latlngList = i.getStringArrayListExtra("latlng");
        itemList = i.getStringArrayListExtra("location");

        LatLng userlocation = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(userlocation).
                title("User location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(userlocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userlocation,13));


        for(int j=0; j<latlngList.size();j++){
            String[] address = itemList.get(j).split("\n");

            String[] result = latlngList.get(j).split(",");
            LatLng placeOfInterest = new LatLng(Double.parseDouble(result[0]),Double.parseDouble(result[1]));
            mMap.addMarker(new MarkerOptions().position(placeOfInterest).title(address[0]).snippet(address[1]));

//            mMap.addMarker(new MarkerOptions().position(placeOfInterest).title(address[0] + "\r\n" + address[1]));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(placeOfInterest));

        }


    }



}

