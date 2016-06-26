package com.fishe.wut2dodemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ResultPage extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ListView listView;
    ArrayList<String> itemList;
    ArrayList<String> latlngList;

    String category;
    String searchResult;
    TextView nameRe;

    public void settingButton(View view){

        Intent i = new Intent(getApplicationContext(), Setting.class);

        startActivity(i);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());

        popup.show();
        popup.setOnMenuItemClickListener(this);

    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Toast.makeText(this,"Changed to : " + item.getTitle(),Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), MapView.class);

        Bundle extras = new Bundle();
        extras.putStringArrayList("latlng", latlngList);
        extras.putStringArrayList("location", itemList);

        i.putExtras(extras);

//        i.putStringArrayListExtra("latlng", latlngList);
        startActivity(i);
//        finish();
        return true;

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
                JSONArray array = new JSONArray(result);

                itemList = new ArrayList<String>();
                latlngList = new ArrayList<String>();

                for(int i =0; i< array.length() ;i++){

                    try {
                        JSONObject jsonPart = array.getJSONObject(i);

                        //add the item into a arraylist of string
                        itemList.add(jsonPart.getString("name")+ "\r\n" + jsonPart.getString("address")
                                + "\r\n" + "Distance away: " + jsonPart.getString("distance") + "km");

                        latlngList.add(jsonPart.getString(("lat_long")));

                    }catch (org.json.JSONException exception){
                        Toast.makeText(getApplicationContext(), "Please key in a valid search term ",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Search.class);
                        //startActivity(intent);
                        setResult(1,intent);
                        finish();
                    }
                }

                //When the search is not valid, go back to search page
                if(itemList.size() == 0){
                    Toast.makeText(getApplicationContext(), "Please key in a valid search term ",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), Search.class);
                    startActivity(i);
                    finish();
                }

                Log.i("Itemlist size = ", String.valueOf(itemList.size()));


                //steps to displat the content of the arraylist as a listView
                final ArrayList<String> addressList = itemList;
                listView = (ListView) findViewById(R.id.listView);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, addressList);
                listView.setAdapter(arrayAdapter);

                //upon clicking on the item
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                        i.putExtra("latlng", latlngList.get(position));
//                        i.putExtra("name", addressList.get(position));

                        startActivity(i);

                        Toast.makeText(getApplicationContext(), "Location: " + addressList.get(position),Toast.LENGTH_LONG).show();
                        Log.i("Location chosen:", addressList.get(position));
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);


        double lat = 1.348796;
        double lng = 103.749428;
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

            String url = "http://orbital_wut_2_do.net16.net/show_details.php?category=" + category
                    +"&latlong="+lat+","+lng;

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

        else{
            searchQuery = extras.getString("name");
            searchQuery = searchQuery.replace("+"," ");
            searchQuery = toUpperCase(searchQuery);

            nameRe.setText(searchQuery);

            searchResult = extras.getString("name").toLowerCase();
            searchResult = searchResult.replace(" ","+");
            String url = "http://orbital_wut_2_do.net16.net/show_search.php?search=" + searchResult
                    +"&latlong="+lat+","+lng;
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
}
