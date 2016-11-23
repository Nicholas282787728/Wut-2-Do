package com.fishe.wut2dodemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

public class SignupActivity extends AppCompatActivity {
    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Set up the login form.
        mUserView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        // in-case function
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            // if user clicks login_button or clicks enter after keying in password, it will log in.
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login_button || id == EditorInfo.IME_NULL) {
                    attemptSignup(textView);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Attempts to register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no registration is made.
     */
    public void attemptSignup(View view) {
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
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // perform the user registration attempt.
            new UserSignupTask(username, password).execute();
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserSignupTask extends AsyncTask<Boolean, Boolean, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private HttpURLConnection con;
        public static final String USER_AGENT = "Mozilla/5.0";
        public static final String mUrl = "http://orbital_wut_2_do.net16.net/copy/login/signup.php";

        UserSignupTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Boolean... unused) {
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
                Log.i("Result", response);

                return response.equals("Account successfully created.");
            }catch (UnsupportedEncodingException e) {
                Log.i("1st Gone", e.toString());
            } catch (IOException e) {
                Log.i("2nd Gone", e.toString());
            }
            // just to catch return type
            return false;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            if (response) {
                // TODO: Auto log-in and direct to main page
                SaveSharedPreference.setUserName(getApplicationContext(),mUsername);
                SaveSharedPreference.setPassWord(getApplicationContext(),mPassword);
                SaveSharedPreference.login(getApplicationContext(),"true");
                finish();
                Intent i = new Intent(getApplicationContext(), QuestionUser.class);
                startActivity(i);
            } else {
                mUserView.setError(getString(R.string.error_sign_up));
                mUserView.requestFocus();
            }
        }
    }
}