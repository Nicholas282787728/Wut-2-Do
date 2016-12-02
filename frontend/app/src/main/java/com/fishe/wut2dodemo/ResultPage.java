package com.fishe.wut2dodemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.maps.model.LatLng;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


class Details{
    String name;
    String address;
    String distance;

    public Details(JSONObject object){
        try {
            this.name = object.getString("name");
            this.address = object.getString("address");
//            this.distance = object.getString("build_id") + "km";
            this.distance = object.getString("distance") + "km";

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {return name;}

    public String getAddress() {return address;}

    public String getDistance() {return distance;}

}

class DetailsAdapter extends ArrayAdapter<Details> {

    /** Inflater for list items */
    private final LayoutInflater inflater;

    /** To cache views of item */
    private static class ViewHolder {
        public TextView name;
        public TextView address;
        public TextView distance;
        /**
         * General constructor
         */
        ViewHolder() {
            // nothing to do here
        }
    }

    // constructor
    public DetailsAdapter(final Context context,
                          final int textViewResourceId,
                          final List<Details> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View itemView = convertView;
        ViewHolder holder = null;
        final Details details = getItem(position);

        if(itemView == null) {
            itemView = this.inflater.inflate(R.layout.list_row_layout, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView)itemView.findViewById(R.id.name);
            holder.address = (TextView)itemView.findViewById(R.id.address);
            holder.distance = (TextView)itemView.findViewById(R.id.distance);

            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        holder.name.setText(details.getName());
        holder.address.setText(details.getAddress());
        holder.distance.setText(details.getDistance());

        return itemView;
    }
}

class DetailReview implements Comparable<DetailReview> {
    String name;
    String address;
    String review;
    double avg_review;

    public DetailReview(JSONObject object){
        try {
            this.name = object.getString("name");
            this.address = object.getString("address");
//            this.review = object.getString("reviews_avg") + "/5";
            this.review = object.getString("reviews_avg");
            this.avg_review = Double.parseDouble(object.getString("reviews_avg"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
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

class DetailRAdapter extends ArrayAdapter<DetailReview> {

    /** Inflater for list items */
    private final LayoutInflater inflater;

    /** To cache views of item */
    private static class ViewHolder {
        public TextView name;
        public TextView address;
       // public TextView rating;
        public RatingBar rate;
        /**
         * General constructor
         */
        ViewHolder() {
            // nothing to do here
        }
    }

    // constructor
    public DetailRAdapter(final Context context,
                          final int textViewResourceId,
                          final List<DetailReview> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View itemView = convertView;
        ViewHolder holder = null;
        final DetailReview details = getItem(position);

        if(itemView == null) {
            itemView = this.inflater.inflate(R.layout.rating_layout, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView)itemView.findViewById(R.id.name);
            holder.address = (TextView)itemView.findViewById(R.id.address);
           // holder.rating = (TextView)itemView.findViewById(R.id.rating);
            holder.rate = (RatingBar)itemView.findViewById(R.id.ratingBar);

            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        holder.name.setText(details.getName());
        holder.address.setText(details.getAddress());
      //  holder.rating.setText(details.getReview());
        holder.rate.setStepSize((float) 0.05);
        holder.rate.setRating(details.getAvg());

        //Log.i("Rating ", String.valueOf(details.getAvg()));
        return itemView;
    }
}


public class ResultPage extends RuntimePermissionsActivity implements LocationGenerator.LocationUpdate {
    ListView listView;
    ArrayList<Details> itemList;
    ArrayList<String> latlngList;
    ArrayList<String> temp;
    ArrayList<String> locationDetails;
    ProgressDialog dialog;
    String category;
    String searchResult;
    TextView nameRe;
    ArrayList<DetailReview> sorting;
    int positionOrigin;
    HashMap<String,Integer> map;
    ArrayList<String> name;
    RandomChoose randomChoose;
    private LocationGenerator locationGenerator;
    private LatLng userCoordinates;

    @Override
    public void onPermissionsGranted(int requestCode) {
        Snackbar.make(findViewById(android.R.id.content), "Permissions Received.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void updateLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        userCoordinates = new LatLng(latitude, longitude);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.mapV:{
                Toast.makeText(this,"Changed to : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MapView.class);

                Bundle extras = new Bundle();
                extras.putStringArrayList("latlng", latlngList);
                extras.putStringArrayList("location", temp);

                i.putExtras(extras);
                startActivity(i);
                return true;
            }
            //sort by rating score
            case R.id.sortRate:{
                Toast.makeText(this,"Changed to : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                final ArrayList<DetailReview> addressList = sorting;
                Collections.sort(addressList);
                DetailRAdapter detailRAdapter = new DetailRAdapter(getBaseContext(), android.R.layout.simple_list_item_1, addressList);
                listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(detailRAdapter);

                //upon clicking on the item
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                        Bundle extras = new Bundle();

                        String locationName = addressList.get(position).getName();
                        int index = map.get(locationName);

                        extras.putString("latlng",latlngList.get(index));
                        extras.putString("info", locationDetails.get(index));
                        extras.putString("location", temp.get(index));
                        i.putExtras(extras);
                        startActivity(i);

                        Toast.makeText(getApplicationContext(), "Location: " + temp.get(index),Toast.LENGTH_LONG).show();
                        Log.i("Location chosen:", String.valueOf(addressList.get(index)));
                    }
                });
                return true;
            }
            //sort by distance
            case R.id.sortDis:{
                Toast.makeText(this,"Changed to : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                final ArrayList<Details> addressList = itemList;
                DetailsAdapter detailsAdapter = new DetailsAdapter(getBaseContext(), android.R.layout.simple_list_item_1, addressList);
                listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(detailsAdapter);

                //upon clicking on the item
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("latlng",latlngList.get(position));
                        extras.putString("info", locationDetails.get(position));
                        extras.putString("location", temp.get(position));
                        i.putExtras(extras);
                        startActivity(i);

                        Toast.makeText(getApplicationContext(), "Location: " + temp.get(position),Toast.LENGTH_LONG).show();
                        Log.i("Location chosen:", String.valueOf(addressList.get(position)));
                    }
                });

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
                if(data>25){
                    for(int i=0;i<22;i++){
                        char current = (char)data;
                        result += current;
                        data = reader.read();
                    }
                    return result;
                }
                else{
                    while(data != -1){
                        char current = (char)data;
                        result += current;
                        data = reader.read();
                    }
                    return result;
                }


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
            positionOrigin = 0;
            map = new HashMap<String,Integer>();
            name = new ArrayList<String>();
            try {
                if(result==null){
                    finish();
                    startActivity(getIntent());
                }
                JSONArray array = new JSONArray(result);
                itemList = new ArrayList<Details>();
                latlngList = new ArrayList<String>();
                temp = new ArrayList<String>();
                locationDetails = new ArrayList<String>();
                sorting = new ArrayList<DetailReview>();

                for(int i =0; i< array.length(); i++){
                    try {
                        JSONObject jsonPart = array.getJSONObject(i);
                        temp.add(jsonPart.getString("name")+ "\r\n" + jsonPart.getString("address")
                                + "\r\n" + "Distance away: " + jsonPart.getString("distance") + "km");

                        name.add(jsonPart.getString("name"));
                        map.put(jsonPart.getString("name"), positionOrigin++);
                        //add the item into a arraylist of string
                        itemList.add(new Details(jsonPart));
                        latlngList.add(jsonPart.getString(("lat_long")));
                        locationDetails.add(jsonPart.getString("website")+ "\r\n" + "Contact: "+jsonPart.getString("tel_num"));
                        //sorting.add(jsonPart.getString(("name"))+ "\r\n" + jsonPart.getString("address")
                        //        + "\r\n" + "Average rating: " + jsonPart.getString("reviews_avg")+"/5");
                        sorting.add(new DetailReview((jsonPart)));

                    }
                    catch (org.json.JSONException exception){
                        Toast.makeText(getApplicationContext(), "No results found ",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Search.class);
                        //startActivity(intent);
                        setResult(1,intent);
                        finish();
                    }

                }


                Log.i("Itemlist size = ", String.valueOf(itemList.size()));

                //steps to display the content of the arraylist as a listView
                final ArrayList<Details> addressList = itemList;
                DetailsAdapter detailsAdapter = new DetailsAdapter(getBaseContext(), android.R.layout.simple_list_item_1, addressList);
                listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(detailsAdapter);

                //upon clicking on the item
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("latlng",latlngList.get(position));
                        extras.putString("info", locationDetails.get(position));
                        extras.putString("location", temp.get(position));

                        i.putExtras(extras);
//                        i.putExtra("latlng", latlngList.get(position));

                        startActivity(i);

                        Toast.makeText(getApplicationContext(), "Location: " + temp.get(position),Toast.LENGTH_SHORT).show();
                        Log.i("Location chosen:", String.valueOf(addressList.get(position)));
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        locationGenerator = new LocationGenerator(this, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Downloading information ");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                nameRe = (TextView)findViewById(R.id.resultPg);
                String searchQuery = "";
                category = "";
                searchResult = "";
                Bundle extras = getIntent().getExtras();
                String code = extras.getString("code");


                Log.i("Code", code);
                if(code.equals("category")) {
                    nameRe.setText(extras.getString("name"));
                    //ensure the string is in proper format to be passed into url
                    category = extras.getString("name").toLowerCase();
                    category = category.replace(" ","+");

                    String url = "http://orbital_wut_2_do.net16.net/copy/output/show_details.php?category=" + category
                            +"&latlong="+userCoordinates.latitude+"," + userCoordinates.longitude;

                    Log.i("URL category", url);

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
                }

                else if(code.equals("search")){
                    searchQuery = extras.getString("name");
                    searchQuery = searchQuery.replace("+"," ");
                    searchQuery = toUpperCase(searchQuery);

                    nameRe.setText(searchQuery);

                    searchResult = extras.getString("name").toLowerCase();
                    searchResult = searchResult.replace(" ","+");
                    String url = "http://orbital_wut_2_do.net16.net/copy/output/show_search.php?search=" + searchResult
                            +"&latlong="+userCoordinates.latitude+","+userCoordinates.longitude;
                    Log.i("URL search", url);

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
                }
                //random result here
                else {
                    randomChoose = new RandomChoose(getApplicationContext());
                    category = randomChoose.getRandomCategory();
                    Log.i("Random", category);
                    nameRe.setText(category);
                    category = category.toLowerCase();
                    category = category.replace(" ","+");
                    String url = "http://orbital_wut_2_do.net16.net/copy/output/show_details.php?category=" + category
                            +"&latlong="+userCoordinates.latitude+","+userCoordinates.longitude;
                    Log.i("URL search", url);

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
                }
                // after finishing, close the progress bar
                dialog.dismiss();
            }
        };

        new Thread(runnable).start();

    }

    //convert the first letter of each word to uppercase
    public static String toUpperCase(String str) {
        String[] arr = str.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    @Override
    protected void onResume() {
        if (locationGenerator != null) {
            locationGenerator.resumeLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        if (locationGenerator != null) {
            locationGenerator.pauseLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        if (locationGenerator != null) {
            locationGenerator.stopLocationUpdates();
        }
    }
}