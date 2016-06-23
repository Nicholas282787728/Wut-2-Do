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

public class Categories extends AppCompatActivity {

    ListView listView;
    ArrayList<String> itemList;
    String category;
    TextView nameCat;

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

                for(int i =0; i< array.length();i++){

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
                        Intent i = new Intent(getApplicationContext(),Main2Activity.class);

                        Bundle extras = new Bundle();
                        extras.putString("name",categoryList.get(position));
                        extras.putString("code","category");
                        i.putExtras(extras);

                        startActivity(i);

                        Toast.makeText(getApplicationContext(), "Category: " + categoryList.get(position),Toast.LENGTH_LONG).show();
                        Log.i("Category chosen", categoryList.get(position));
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
        setContentView(R.layout.activity_categories);

        nameCat = (TextView)findViewById(R.id.categoryPg);


        category = "";
        Intent receive = getIntent();
        category = receive.getStringExtra("name");

        Log.i("Name",category);
        nameCat.setText(receive.getStringExtra("name"));

        String url = "http://orbital_wut_2_do.net16.net/show_categories.php?genre="+category;
        Log.i("URL",url);

        DownloadTask task = new DownloadTask();

        try {
            task.execute(url).get();

        } catch (InterruptedException e) {
            Log.i("Interrupted",category);
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.i("Execution",category);
        }
    }
}
