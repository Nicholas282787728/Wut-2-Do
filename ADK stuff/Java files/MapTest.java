package com.fishe.testinglink2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MapTest extends AppCompatActivity {

    TextView name;

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
        setContentView(R.layout.activity_map_test);

        name = (TextView)findViewById(R.id.mapText);
        Intent receive = getIntent();
        name.setText(receive.getStringExtra("name"));

    }
}
