package com.fishe.testinglink2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowAll extends AppCompatActivity {

    String line;
    TextView view, name;
    ListView listView;

    public void settingButton(View view){

        Intent i = new Intent(getApplicationContext(), Setting.class);

        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        name = (TextView)findViewById(R.id.textView3);
        Intent receive = getIntent();
        name.setText(receive.getStringExtra("name"));

        listView = (ListView) findViewById(R.id.listView2);

        try {
            InputStream is = getAssets().open("cinemaX.txt");
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            String text = new String(buffer);

            String[] items = text.split("\n");
            final List<String> itemList = new ArrayList<String>(Arrays.asList(items));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getApplicationContext(),MapTest.class);
                    i.putExtra("name", itemList.get(position));
                    startActivity(i);

                    Toast.makeText(getApplicationContext(), "Map of : " + itemList.get(position),Toast.LENGTH_LONG).show();
                    Log.i("Location displayed:", itemList.get(position));

                }
            });


        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
    }
}
