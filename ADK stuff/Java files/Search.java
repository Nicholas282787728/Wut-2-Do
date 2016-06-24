package com.fishe.wut2dodemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Search extends AppCompatActivity {

    EditText searchResult;
    String result;

    public void search(View view){

        Log.i("Search",searchResult.getText().toString());
        result = searchResult.getText().toString();
        result = result.replace(" ","+");
        Log.i("Result", result);

        Intent i = new Intent(getApplicationContext(), ResultPage.class);
        Bundle extras = new Bundle();
        extras.putString("name",result);
        extras.putString("code","search");
        i.putExtras(extras);


        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchResult = (EditText)findViewById(R.id.editText);

    }
}
