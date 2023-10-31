package com.example.newspaperdeliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class Download_Order_Details extends AppCompatActivity {
Button show,LOCATION;
TextView name,CustomerName,Copy,Stat;

String latitude,longitude;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PHP_URL = "http://192.168.1.168:8080/Newspaper_delivery_App/show.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_order_details);
        name = findViewById(R.id.txtname1);
        CustomerName = findViewById(R.id.CustomerName1);
        Copy = findViewById(R.id.Copies);
        Stat = findViewById(R.id.Status);
        show = findViewById(R.id.btnShow_order_details);
        LOCATION = findViewById(R.id.BTNLOCATION);

        LOCATION.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LOCATION.class);
                intent.putExtra("lat",latitude);
                intent.putExtra("long",longitude);
                startActivity(intent);
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new FetchDataTask().execute();

            }
        });
    }
    private class FetchDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String response = "";

            try {
                URL url = new URL(PHP_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                reader.close();
                inputStream.close();

                response = stringBuilder.toString();

            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // Extract data from JSON object
                    String newspaperName = jsonObject.getString("newspaperName");
                    String Copies = jsonObject.getString("Copies");
                    String Status = jsonObject.getString("Status");
                     latitude = jsonObject.getString("latitude");
                     longitude = jsonObject.getString("longitude");
                    name.setText("Newspaper Name = "+newspaperName);
                    Copy.setText("Copies = 2");
                    Stat.setText("Status = "+Status);
                    CustomerName.setText("Customer Name = Thilina Supunsara");


                }

            } catch (JSONException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }
        }
    }
}