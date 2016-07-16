package com.fishe.wut2dodemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {


    public void search(View view){

        Intent i = new Intent(getApplicationContext(), Search.class);

        startActivity(i);
    }
    public void category(View view){

        Intent i = new Intent(getApplicationContext(), Genre.class);

        startActivity(i);
    }

    public void random(View view){

        Intent i = new Intent(getApplicationContext(), Genre.class);

        startActivity(i);
    }

    public void login(View view){

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);

        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
