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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class ReviewActivity extends AppCompatActivity {

    private EditText mUserView;
    private RatingBar mRatingBar;
    private EditText mReviewView;
    private String postal_code;
    private String unit_no;
    private String shop_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);


        Intent receive = getIntent();

        postal_code = receive.getStringExtra("postal");
        unit_no = receive.getStringExtra("unit_no");
        shop_name = receive.getStringExtra("name");

        Log.i("Postal code ", postal_code);
        Log.i("Unit Num", unit_no);
        Log.i("Name", shop_name);

        // Set up the login form.

        mRatingBar = (RatingBar) findViewById(R.id.ratingbar);
        mReviewView = (EditText) findViewById(R.id.review);

        // if user clicks on 'enter' or the submit button, it will submit the review.
        mReviewView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            // if user clicks login_button or clicks enter after keying in password, it will log in.
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.submit_button || id == EditorInfo.IME_NULL) {
                    submitReview(textView);
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
    public void submitReview(View view) {
        // Reset errors.
        mReviewView.setError(null);

        // Store values at the time of the login attempt.
//        String username = mUserView.getText().toString();
        String review = mReviewView.getText().toString();

        String username = SaveSharedPreference.getUserName(ReviewActivity.this);

        boolean cancel = false;
        View focusView = null;

        Character c = username.charAt(0);
        Character d = username.charAt(username.length() - 1);

        //TODO: remove this part after we only allow users to post


        /*else if (review.length() < 50) {
            mReviewView.setError(getString(R.string.error_invalid_review));
            focusView = mUserView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }
/*
        // Check for valid username.
        for (int i = 1; i < username.length() - 1; i++) {
            c = username.charAt(i);
            if (!Character.isAlphabetic(c) && !Character.isDigit(c) && c != ' ' && c != '_' && c != '-') {
                mUserView.setError(getString(R.string.error_invalid_username_3));
                focusView = mUserView;
                cancel = true;
            }
        }*/

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Log.i("username", username);
            Log.i("Rating", mRatingBar.getRating() + "");
            Log.i("Review", review);
            Log.i("Postal_code", postal_code);
            Log.i("Unit_no", unit_no);
            Log.i("Name", shop_name);

            // perform the user registration attempt.
            new ReviewTask(username, (int) mRatingBar.getRating(), review, postal_code, unit_no,shop_name).execute();
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class ReviewTask extends AsyncTask<Boolean, Boolean, Boolean> {

        private final String mUsername;
        private final Integer mNumStars;
        private final String mReview;
        private final String mDate;
        private final String mPostalCode;
        private final String mUnitNo;
        private final String mShopName;

        public static final String USER_AGENT = "Mozilla/5.0";
        public static final String mUrl = "http://orbital_wut_2_do.net16.net/copy/review/review.php";

        ReviewTask(String username, int numStars, String review, String postal_code, String unit_no, String shop_name) {
            mUsername = username;
            mNumStars = numStars;
            mReview = review;
            mPostalCode = postal_code;
            mUnitNo = unit_no;
            // format to SG dates
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            mDate = df.format(Calendar.getInstance(TimeZone.getTimeZone("SGT"), new Locale("English", "Singapore")).getTime());
            mShopName = shop_name;
        }

        @Override
        protected Boolean doInBackground(Boolean... unused) {
            try {
                // connect to web page
                URL url = new URL(mUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Language", "UTF-8");
                StringBuilder params = new StringBuilder("");
                params.append("&" + "username" + "=");
                params.append(URLEncoder.encode(mUsername, "UTF-8"));
                params.append("&" + "date" + "=");
                params.append(URLEncoder.encode(mDate, "UTF-8"));
                params.append("&" + "num_stars" + "=");
                params.append(URLEncoder.encode(mNumStars.toString(), "UTF-8"));
                params.append("&" + "review" + "=");
                params.append(URLEncoder.encode(mReview, "UTF-8"));
                params.append("&" + "postal_code" + "=");
                params.append(URLEncoder.encode(mPostalCode, "UTF-8"));
                params.append("&" + "unit_no" + "=");
                params.append(URLEncoder.encode(mUnitNo, "UTF-8"));
                params.append("&" + "shop_name" + "=");
                params.append(URLEncoder.encode(mShopName, "UTF-8"));



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

                // PHP response if review successfully posted
                return response.equals("Success");
            } catch (UnsupportedEncodingException e) {
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
                Toast.makeText(getApplicationContext(),
                        "Your review has been posted and will appear shortly. Thank you.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Log.i("Huh what's wrong", "I don't know");
            }
        }
    }
}