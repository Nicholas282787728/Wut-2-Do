package com.fishe.wut2dodemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fishe.wut2dodemo.user.LoginActivity;
import com.fishe.wut2dodemo.user.SaveSharedPreference;

import java.util.ArrayList;

public class Genre extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (SaveSharedPreference.getIsLogIn(Genre.this).equals("true")){
            getMenuInflater().inflate(R.menu.logout_toobar, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.login_toolbar, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout: {
                SaveSharedPreference.logout(getApplicationContext(),"false");
                finish();
                startActivity(getIntent());
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.userInfo: {
                Intent i = new Intent(getApplicationContext(), Setting.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "User Info", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.login:{
             //   Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);
        ListView myListView = (ListView)findViewById(R.id.listView);

        final ArrayList<String> genre = new ArrayList<String>();

        //genre.add("All");
        genre.add("Games");
        genre.add("History");
        genre.add("Movie");
        genre.add("Music");
        genre.add("Nature");
        genre.add("Puzzle");
        genre.add("Sports");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, genre);

        myListView.setAdapter(arrayAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                              @Override
                                              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                  Intent i = new Intent(getApplicationContext(), Category.class);
                                                  i.putExtra("name", genre.get(position));

                                                  startActivity(i);

                                                  Log.i("Genre chosen", genre.get(position));

            /*
            if(position == 0){
                Intent i = new Intent(getApplicationContext(), ResultPage.class);

                //send directly to result page
                Bundle extras = new Bundle();
                extras.putString("name",genre.get(position));
                extras.putString("code","category");
                extras.putDouble("lat",mLatitude);
                extras.putDouble("lng",mLongitude);

                i.putExtras(extras);

                startActivity(i);

              //  Toast.makeText(getApplicationContext(), "Genre " + genre.get(position), Toast.LENGTH_SHORT).show();
                Log.i("Genre chosen", genre.get(position));

            }
            else {
                Intent i = new Intent(getApplicationContext(), Category.class);
                i.putExtra("name", genre.get(position));

                startActivity(i);

              //  Toast.makeText(getApplicationContext(), "Genre " + genre.get(position), Toast.LENGTH_SHORT).show();
                Log.i("Genre chosen", genre.get(position));
            }*/
            }
            }
        );
    }

}
