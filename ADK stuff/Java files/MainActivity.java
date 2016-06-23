package com.fishe.testinglink2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public void search(View view){

        Intent i = new Intent(getApplicationContext(), Search.class);

        startActivity(i);
    }
    public void category(View view){

        Intent i = new Intent(getApplicationContext(), Category.class);

        startActivity(i);
    }

    public void random(View view){

        Intent i = new Intent(getApplicationContext(), Category.class);

        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
