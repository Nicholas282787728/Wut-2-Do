package com.fishe.wut2dodemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Genre extends AppCompatActivity {

    public void backButton(View view){

        Intent i = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(i);
    }

    public void settingButton(View view){

        Intent i = new Intent(getApplicationContext(), Setting.class);

        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);
        ListView myListView = (ListView)findViewById(R.id.listView);

        final ArrayList<String> genre = new ArrayList<String>();

        genre.add("All");
        genre.add("Puzzle");
        genre.add("Games");
        genre.add("Sports");
        genre.add("Movie");
        genre.add("Music");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, genre);

        myListView.setAdapter(arrayAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                              @Override
                                              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(position == 0){
                Intent i = new Intent(getApplicationContext(), ResultPage.class);

                //send directly to result page
                Bundle extras = new Bundle();
                extras.putString("name",genre.get(position));
                extras.putString("code","category");
                i.putExtras(extras);

                startActivity(i);

                Toast.makeText(getApplicationContext(), "Genre " + genre.get(position), Toast.LENGTH_LONG).show();
                Log.i("Genre chosen", genre.get(position));

            }
            else {
                Intent i = new Intent(getApplicationContext(), Category.class);
                i.putExtra("name", genre.get(position));

                startActivity(i);

                Toast.makeText(getApplicationContext(), "Genre " + genre.get(position), Toast.LENGTH_LONG).show();
                Log.i("Genre chosen", genre.get(position));
            }}
                                          }
        );




    }
}
