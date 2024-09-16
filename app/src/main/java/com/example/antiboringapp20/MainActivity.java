package com.example.antiboringapp20;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mTextView;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private static String activity;
    private SwitchCompat aiSwitch;
    private static TextView poph;
    private static TextView pop;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Button taskButton = findViewById(R.id.button);
        Button learnButton = findViewById(R.id.button2);
        mTextView = findViewById(R.id.textView);
        mTextView1 = findViewById(R.id.textView4);
        mTextView2 = findViewById(R.id.textView6);
        mTextView3 = findViewById(R.id.textView8);
        mTextView4 = findViewById(R.id.textView10);
        aiSwitch = findViewById(R.id.switch1);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        poph = popupView.findViewById(R.id.textView12);
        pop = popupView.findViewById(R.id.popup_text);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        mainHandler = new Handler(Looper.getMainLooper());

        taskButton.setOnClickListener(view -> {
            fetchDataFromApi();
            vib.vibrate(80);
        });
        learnButton.setOnClickListener(view -> {
            boolean switchState = aiSwitch.isChecked();
            if(switchState){
//                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//                fetchHowto();
            }
            else {
                Uri uriUrl = Uri.parse("https://www.google.com/search?q=" + "How to " + activity);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }

    private class FetchDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String responseBody = "";
            HttpURLConnection connection = null;
            System.out.println("Fetching Data from BoredAPI");
            try {
                String apiUrl2 = "https://bored-api.appbrewery.com/random";
                URL apiUrl = new URL(apiUrl2);
                connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                        System.out.println(line);
                    }
                    responseBody = responseBuilder.toString();
                } else {
                    Log.e(TAG, "Failed to fetch data: " + responseCode);
                    return null;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage(), e);
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return responseBody;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                parseAndDisplayResult(result);
            } else {
                postErrorToUi();
            }
        }
    }

    private void fetchDataFromApi() {
        new FetchDataTask().execute();
    }


        private void parseAndDisplayResult(String result) {
            try {
                JSONObject json = new JSONObject(result);
                final String activit = json.getString("activity");
                final String typeo = json.getString("type");
                final String parti = json.getString("participants");
                String price = json.getString("price");
                double priceInt = Double.parseDouble(price);
                priceInt *= 1500;
                final String finalPrice = "₹" + priceInt;

                String acc = json.getString("accessibility");

                mainHandler.post(() -> {
                    mTextView.setText(activit);
                    mTextView1.setText(typeo);
                    mTextView2.setText(parti);
                    mTextView3.setText(finalPrice);
                    mTextView4.setText(acc);
                });
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage() + "| Result: " + result);
                postErrorToUi();
            }
        }

        private void postErrorToUi() {
            mainHandler.post(() -> mTextView.setText(R.string.error));
        }

//        private void fetchHowTo() {
//
//                String completedTexts = service.createCompletion(completionRequest).getChoices().get(0).toString();
//
//                mainHandler.post(() -> {
//                    poph.setText(activity);
//                    pop.setText(completedTexts);
//                }
//        }
    }