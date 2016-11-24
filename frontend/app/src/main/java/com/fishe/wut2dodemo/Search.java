package com.fishe.wut2dodemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fishe.wut2dodemo.user.LoginActivity;
import com.fishe.wut2dodemo.user.SaveSharedPreference;

public class Search extends AppCompatActivity {

    EditText searchResult;
    String result;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (SaveSharedPreference.getIsLogIn(Search.this).equals("true")){
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
               // Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*
    public void settingButton(View viewPop) {

        if(SaveSharedPreference.getIsLogIn(Search.this).equals("true"))
        {
            PopupMenu popup = new PopupMenu(this, viewPop);
            MenuInflater inflaterPop = popup.getMenuInflater();
            inflaterPop.inflate(R.menu.logout, popup.getMenu());

            popup.show();
            popup.setOnMenuItemClickListener(this);

        }
        else  //if not logged in
        {
            PopupMenu popup = new PopupMenu(this, viewPop);
            MenuInflater inflaterPop = popup.getMenuInflater();
            inflaterPop.inflate(R.menu.userinfo, popup.getMenu());

            popup.show();
            popup.setOnMenuItemClickListener(this);
        }

    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:{
                SaveSharedPreference.logout(getApplicationContext(),"false");
                finish();
                startActivity(getIntent());
                return true;
            }
            case R.id.userInfo:{
                Intent i = new Intent(getApplicationContext(), Setting.class);

                startActivity(i);
                return true;
            }
            case R.id.login:{
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);

            }
            default: break;
        }
        return false;
    }
*/
    public void search(View view){

        Log.i("Search",searchResult.getText().toString());
        result = searchResult.getText().toString();
        result = result.replace(" ","+");
        Log.i("Result", result);

        // Check if the user entered username.
        if (TextUtils.isEmpty(result)) {
            searchResult.setError(getString(R.string.enter));
        }

        else{
            searchResult.setError(null);
            Intent i = new Intent(getApplicationContext(), ResultPage.class);
            Bundle extras = new Bundle();
            extras.putString("name",result);
            extras.putString("code","search");
            i.putExtras(extras);
            startActivityForResult(i, 1);
        }

//        startActivity(i);
//        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==1 && resultCode == 1)
        {
            //
            startActivity(getIntent());
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchResult = (EditText)findViewById(R.id.editText);

        // in-case function
        searchResult.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            // if user clicks login_button or clicks enter after keying in password, it will log in.
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.imageButton || id == EditorInfo.IME_ACTION_DONE) {
                    search(textView);
                    return true;
                }
                return false;
            }
        });


    }
}
