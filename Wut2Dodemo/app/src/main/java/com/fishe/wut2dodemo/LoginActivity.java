package com.fishe.wut2dodemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.facebook.FacebookSdk;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        // in-case function
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            // if user clicks login_button or clicks enter after keying in password, it will log in.
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login_button || id == EditorInfo.IME_NULL) {
                    attemptLogin(textView);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(View view) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check if the user entered username.
        if (TextUtils.isEmpty(username)) {
            mUserView.setError(getString(R.string.error_invalid_username));
            focusView = mUserView;
            cancel = true;
        }

        // Check if the user entered password.
        else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute();
        }
    }

    public void signup(View view) {
        Intent i = new Intent(getApplicationContext(), SignupActivity.class);
        finish();
        startActivity(i);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, String, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private HttpURLConnection con;
        public static final String USER_AGENT = "Mozilla/5.0";
        public static final String mUrl = "http://orbital_wut_2_do.net16.net/copy/login/login.php";

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... unused) {
            try {
                // connect to web page
                URL url = new URL(mUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Language", "UTF-8");
                StringBuilder params = new StringBuilder("");
                params.append("&" + "username" + "=");
                params.append(URLEncoder.encode(mUsername, "UTF-8"));
                params.append("&" + "password" + "=");
                params.append(URLEncoder.encode(mPassword, "UTF-8"));

                // send POST data
                con.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
                outputStreamWriter.write(params.toString());
                outputStreamWriter.flush();

                // check for response
                Integer responseCode = con.getResponseCode();
                Log.i("URL : ", mUrl);
                Log.i("parameters : ", params.toString());
                Log.i("Response Code : ", responseCode.toString());

                // retrieving server response
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String response = in.readLine();
                in.close();
                Log.i("Result : ", response);
                if(response.equals("Log in successful.")) {
                    SaveSharedPreference.setUserName(getApplicationContext(),mUsername);
                    SaveSharedPreference.setPassWord(getApplicationContext(),mPassword);
                    SaveSharedPreference.login(getApplicationContext(),"true");
                    return true;
                } else{
                    return false;
                }
            } catch (UnsupportedEncodingException e) {
                Log.i("1st Gone", e.toString());
            } catch (IOException e) {
                Log.i("2nd Gone", e.toString());
            }
            // just to catch return type
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean response) {
            mAuthTask = null;

            if (response) {
                // TODO: stay logged in
                finish();
            } else {
                mUserView.setError(getString(R.string.error_log_in));
                mUserView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

