package com.fishe.testinglink2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class Main2Activity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> itemList;

    String category;
    String searchResult;
    TextView nameRe;

    public void settingButton(View view){

        Intent i = new Intent(getApplicationContext(), Setting.class);

        startActivity(i);
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

                for(int i =0; i< array.length() ;i++){

                    try {
                        JSONObject jsonPart = array.getJSONObject(i);
                    }catch (org.json.JSONException exception){
                        Toast.makeText(getApplicationContext(), "Please key in a valid search term ",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Search.class);
                        startActivity(intent);
                        finish();
                    }

                    JSONObject jsonPart = array.getJSONObject(i);

                    //Double distance = Double.parseDouble(jsonPart.getString("distance"))/1000;
                    itemList.add(jsonPart.getString("name")+ "\r\n" + jsonPart.getString("address")
                    + "\r\n" + "Distance away: " + jsonPart.getString("distance") + "km");

                }

                //When the search is not valid
                if(itemList.size() == 0){
                    Toast.makeText(getApplicationContext(), "Please key in a valid search term ",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), Search.class);
                    startActivity(i);
                }

                Log.i("Itemlist size = ", String.valueOf(itemList.size()));

                final ArrayList<String> addressList = itemList;
                listView = (ListView) findViewById(R.id.listView);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, addressList);
                listView.setAdapter(arrayAdapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                        i.putExtra("name", addressList.get(position));
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
        setContentView(R.layout.activity_main2);

        nameRe = (TextView)findViewById(R.id.resultPg);
        String searchQuery = "";
        category = "";
        searchResult = "";
        Bundle extras = getIntent().getExtras();
        String code = extras.getString("code");
        Log.i("Code", code);

        if(code.equals("category")) {
            nameRe.setText(extras.getString("name"));
            category = extras.getString("name").toLowerCase();
            category = category.replace(" ","+");
//            String url = "http://orbital_wut_2_do.net16.net/show_details.php?category=" + category
//                    +"&latlong=123.1,321.2";

            String url = "http://orbital_wut_2_do.net16.net/show_details.php?category=" + category
                    +"&latlong=1.348796,103.749428";

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
                    +"&latlong=1.348796,103.749428";
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
