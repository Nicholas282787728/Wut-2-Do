package com.fishe.wut2dodemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    TextView Username;
    TextView Favourite;
    TextView Frequent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.userinfo_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            /*
            case R.id.logout: {
                SaveSharedPreference.logout(getApplicationContext(),"false");
                finish();
                startActivity(getIntent());
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                return true;
            }
            */
            case R.id.done: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Username = (TextView) findViewById(R.id.textView2);
        Favourite = (TextView) findViewById(R.id.textView4);
        Frequent = (TextView) findViewById(R.id.textView5);

        Username.setText("Username: "+SaveSharedPreference.getUserName(getApplicationContext()));
        Favourite.setText("Favourite Category: "+QuestionSharedPreference.getFavourite(getApplicationContext()));
        Frequent.setText("Most Frequent Search: " +QuestionSharedPreference.getMostFreq(getApplicationContext()));

    }
}
