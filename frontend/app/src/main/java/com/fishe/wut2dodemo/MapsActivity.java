package com.fishe.wut2dodemo;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DetailMapReview implements Comparable<DetailReview> {
    String name;
    String date;
    String review;
    double avg_review;
    String text;

    public DetailMapReview(JSONObject object){
        /*
       reviewList.add("Name: "+jsonPart.getString("name")+ "\r\n" +  "\r\n" +"Date: "+jsonPart.getString("date")
       + "                  Rating score: " + jsonPart.getString("num_stars")+"/5"
       + "\r\n" + "\r\n" + jsonPart.getString("review"));

        */
        try {
            this.name = object.getString("name");
            this.date = object.getString("date");
            this.review = object.getString("num_stars");
            if(object.getString("num_stars").equals("null")){
                this.avg_review = 0;
            }
            else {
                this.avg_review = Double.parseDouble(object.getString("num_stars"));
            }
            this.text = object.getString("review");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getText() {
        return text;
    }

    public String getReview() {
        if(review.equals("0")){
            return "-";
        }else{
            return review;
        }
    }

    public float getAvg(){ return (float) avg_review;}

    public int compareTo(DetailReview other) {
        return other.review.compareTo(this.review);
    }

}

class DetailMapAdapter extends ArrayAdapter<DetailMapReview> {

    /** Inflater for list items */
    private final LayoutInflater inflater;

    /** To cache views of item */
    private static class ViewHolder {
        public TextView name;
        public TextView date;
        public TextView review;
        public RatingBar rate;
        /**
         * General constructor
         */
        ViewHolder() {
            // nothing to do here
        }
    }

    // constructor
    public DetailMapAdapter(final Context context,
                          final int textViewResourceId,
                          final List<DetailMapReview> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View itemView = convertView;
        ViewHolder holder = null;
        final DetailMapReview details = getItem(position);

        if(itemView == null) {
            itemView = this.inflater.inflate(R.layout.map_rate, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView)itemView.findViewById(R.id.name);
            holder.date = (TextView)itemView.findViewById(R.id.date);
            holder.review = (TextView)itemView.findViewById(R.id.reviews);
            holder.rate = (RatingBar)itemView.findViewById(R.id.ratingBar);

            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        holder.name.setText(details.getName());
        holder.date.setText(details.getDate());
        holder.review.setText(details.getText());
        holder.rate.setStepSize((float) 0.05);
        holder.rate.setRating(details.getAvg());

        //Log.i("Rating ", String.valueOf(details.getAvg()));
        return itemView;
    }
}

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    ListView listView, listView2;
    private GoogleMap mMap;
    double lat=0,lng=0;
    String address;
    String postal;
    String latlng;
    String addressName;
    ArrayList<String> temp;
    String locationInfo;
    String url;
    String unit_no;
    ArrayList<String> reviewList;
    ArrayList<DetailMapReview> itemList;
    String[] addressResult;
    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char)data;
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
                if(result==null){
                    Log.i("Error ", "json testing");
                    finish();
                    startActivity(getIntent());
                }
                JSONArray array = new JSONArray(result);
                itemList = new ArrayList<DetailMapReview>();

                reviewList = new ArrayList<String>();

                for(int i =0; i< array.length();i++){

                    JSONObject jsonPart = array.getJSONObject(i);
                    itemList.add(new DetailMapReview(jsonPart));

                    reviewList.add("Name: "+jsonPart.getString("name")+ "\r\n" +  "\r\n" +"Date: "+jsonPart.getString("date")
                            + "                  Rating score: " + jsonPart.getString("num_stars")+"/5"
                            + "\r\n" + "\r\n" + jsonPart.getString("review"));

                }

              /*  ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, reviewList);
                listView2 = (ListView) findViewById(R.id.listView2);
                listView2.setAdapter(arrayAdapter2);
                */
                final ArrayList<DetailMapReview> addressList = itemList;
                DetailMapAdapter detailsAdapter = new DetailMapAdapter(getBaseContext(), android.R.layout.simple_list_item_1, addressList);
                listView2 = (ListView) findViewById(R.id.listView2);
                listView2.setAdapter(detailsAdapter);

            } catch (JSONException e) {
                Log.i("Error ", "json failed");
                e.printStackTrace();
            }

        }
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(SaveSharedPreference.getIsLogIn(MapsActivity.this).equals("true"))
        {
            getMenuInflater().inflate(R.menu.review_add, menu);
        }

        else
        {
            getMenuInflater().inflate(R.menu.login_toolbar, menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.login:{
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);
                return true;
            }

            case R.id.addR:{
                Intent i = new Intent(getApplicationContext(), ReviewActivity.class);
                Bundle extras = new Bundle();
                extras.putString("postal",postal);
                extras.putString("unit_no", unit_no);
                extras.putString("name",addressResult[0]);
                Log.i("Postal code ", postal);
                Log.i("Unit Num", unit_no);
                Log.i("Name", addressName);
                i.putExtras(extras);
                startActivity(i);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent receive = getIntent();
        latlng = receive.getStringExtra("latlng");
        locationInfo = receive.getStringExtra("info");
        address = receive.getStringExtra("location");
        addressResult = address.split("\n");
        postal = findPostal(addressResult[1]);
        unit_no = findUnit(addressResult[1]);
        String[] locationinformation = locationInfo.split("\n");
        addressName = addressResult[0];
        Log.i("shop name", addressName);

        addressName = addressResult[0].toLowerCase();
        Log.wtf("shopv2 name", addressName);

        addressName = addressName.replace(" ","+");
        Log.d("shopv3 name", addressName);

        //  final ArrayList<String>
        temp = new ArrayList<String >();
        if(locationinformation[0].trim().isEmpty()){
            locationinformation[0] = "Website: N/A";
        }

        for(int i = 0;i<locationinformation.length;i++){
            temp.add(locationinformation[i]);
        }


        url = "http://orbital_wut_2_do.net16.net/copy/output/show_reviews.php?postal_code=" + postal + "&unit_no="+ unit_no +
        "&shop_name=" + addressName;
        //url = "http://orbital_wut_2_do.net16.net/copy/output/show_reviews.php?shop_name="
          //     +"east+coast+park"+"&unit_no=&postal_code=449876";


        DownloadTask task = new DownloadTask();

        try {
            task.execute(url).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, temp);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String web = temp.get(position);
                if(position==0){
                    if(!web.equals("Website: N/A")) {
                        Uri uri = Uri.parse(temp.get(position));
                        Log.i("Check", "Test");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(browserIntent);
                    }
                }
                /*
                if(position==1){
                   String number = Define here;
                   Intent intent = new Intent(Intent.ACTION_CALL);
                   intent.setData(Uri.parse("Contact: " + number));
                   startActivity(intent);
                }*/
            }
        });


        String[] result = latlng.split(",");
        Bundle extras = getIntent().getExtras();
        GoogleLocationApi googleLocationApi = GoogleLocationApi.getInstance();
        double lat = googleLocationApi.getUserCoordinates().getLatitude();
        double lng = googleLocationApi.getUserCoordinates().getLongitude();


        LatLng userlocation = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(userlocation).
                title("User location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        LatLng placeOfInterest = new LatLng(Double.parseDouble(result[0]),Double.parseDouble(result[1]));
        mMap.addMarker(new MarkerOptions().position(placeOfInterest).title(addressResult[0]).snippet(addressResult[1]));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(placeOfInterest));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeOfInterest,16));

    }

    public String findPostal(String str){
        Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9][0-9][0-9]");
        Matcher matcher = pattern.matcher(str);

        if(matcher.find()){
            return matcher.group();
        }
        else{
            return "";
        }
    }

    public String findUnit(String str) {
        Pattern pattern = Pattern.compile("[0-9][0-9][-][0-9][0-9]");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            int i = matcher.end();
            while (str.charAt(i) != ' ') {
                i++;
            }
            if (str.charAt(i - 1) == ',')
                i--;

            String toReturn = str.substring(matcher.start(), i);
            return toReturn;
        } else {
            return "";
        }
    }

    @Override
    protected void onPause() {
        GoogleLocationApi.pauseLocationUpdates();
    }

    @Override
    protected void onResume() {
        GoogleLocationApi.resumeLocationUpdates();
    }

    @Override
    protected void onStop() {
        GoogleLocationApi.stopLocationUpdates();
    }
}
/*
        address = receive.getStringExtra("name");
        String[] result = address.split("\n");
        postal = findPostal(result[1]);


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