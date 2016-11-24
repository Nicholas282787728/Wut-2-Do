package com.fishe.wut2dodemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends Location {

    private ImageButton search, genre, random, login;
    final String PREF_NAME = "MyPrefs";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (SaveSharedPreference.getIsLogIn(MainActivity.this).equals("true")){
            getMenuInflater().inflate(R.menu.logout_toobar, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.login_toolbar, menu);
        }
        getMenuInflater().inflate(R.menu.extras,menu);
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
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);
                return true;
            }
            case R.id.credits:{
                Intent i = new Intent(getApplicationContext(), About.class);
                startActivity(i);
                return true;
            }
            case R.id.tutorial:{
                doTutorial();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void search(View view){

        Intent i = new Intent(getApplicationContext(), Search.class);

        startActivity(i);
    }
    public void category(View view){

        Intent i = new Intent(getApplicationContext(), Genre.class);

        startActivity(i);
    }

    public void random(View view){

        Intent i = new Intent(getApplicationContext(), ResultPage.class);
        Bundle extras = new Bundle();
        extras.putString("code", "random");
        extras.putDouble("lat", googleLocationApi.getUserCoordinates().getLatitude());
        extras.putDouble("lng", googleLocationApi.getUserCoordinates().getLongitude());
        i.putExtras(extras);

        startActivity(i);
    }

    public void login(View view){

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        //Intent i = new Intent(getApplicationContext(), QuestionUser.class);

        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
   //     getSupportActionBar().setLogo(R.drawable.app);
   //     getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_main);

        search = (ImageButton)findViewById(R.id.search);
        genre = (ImageButton)findViewById(R.id.genre);
        random = (ImageButton)findViewById(R.id.random);
        login = (ImageButton)findViewById(R.id.login);

        SharedPreferences settings = getSharedPreferences(PREF_NAME,0);

        if(settings.getBoolean("first_time",true)){
            doTutorial();
            settings.edit().putBoolean("first_time",false).apply();
        }

        googleLocationApi = GoogleLocationApi.getInstance(this);

        //if not logged in
        if(SaveSharedPreference.getIsLogIn(MainActivity.this).equals("true")) {
            Toast.makeText(getApplicationContext(), "Welcome Back " + SaveSharedPreference.getUserName(MainActivity.this),Toast.LENGTH_SHORT).show();
            Log.i("User", "Logged in");
        } else {
            QuestionSharedPreference.setDefault(getApplicationContext());
            Log.i("User", "Logged out");
        }
    }

    private void doTutorial(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(0);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);

        sequence.addSequenceItem(search, "Search",
                "Enter names of towns or buildings to find entertainment near the location", "Got it!");
        sequence.addSequenceItem(genre, "Genre",
                "Find activities to do near you based on the activities' genre", "Got it!");
        sequence.addSequenceItem(random, "What's Fun?",
                "Feeling lucky? Click to generate a random activity near you", "Got it!");
        sequence.addSequenceItem(login, "Log In",
                "Create an account or log in", "Got it!");

        sequence.start();
    }
}
