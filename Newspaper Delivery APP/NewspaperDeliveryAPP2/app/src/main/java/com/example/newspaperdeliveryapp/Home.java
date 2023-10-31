package com.example.newspaperdeliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import okhttp3.FormBody;

import okhttp3.RequestBody;


public class Home extends AppCompatActivity {

    private static final String DOWNLOAD_URL = "http://192.168.1.168:8080/Newspaper_delivery_App/download.php";
    private static final String URL = "http://192.168.1.168:8080/Newspaper_delivery_App/Update.php";

    String stat;
    Button downloadButton,COMPLETED,FAILED,logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        downloadButton = findViewById(R.id.BTNDownload);
        COMPLETED = findViewById(R.id.BTNCOMPLETED);
        FAILED = findViewById(R.id.BTNFAILED);
        logout = findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        FAILED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient client = new OkHttpClient();

                String url = "http://192.168.1.168:8080/Newspaper_delivery_App/Update.php";

                RequestBody formBody = new FormBody.Builder()
                        .add("new_value", "FAILED")
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Handle network failure
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Handle response
                        if (response.isSuccessful()) {
                            final String responseBody = response.body().string();

                            // Update UI on the main thread if needed
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Handle the response here
                                    Toast.makeText(Home.this, responseBody, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Handle unsuccessful response
                            // You can retrieve the error code and message if needed
                            int statusCode = response.code();
                            String errorMessage = response.message();
                        }
                    }
                });
            }

        });
        COMPLETED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient client = new OkHttpClient();

                String url = "http://192.168.1.168:8080/Newspaper_delivery_App/Update.php";

                RequestBody formBody = new FormBody.Builder()
                        .add("new_value", "COMPLETED")
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Handle network failure
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Handle response
                        if (response.isSuccessful()) {
                            final String responseBody = response.body().string();

                            // Update UI on the main thread if needed
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Handle the response here
                                    Toast.makeText(Home.this, responseBody, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Handle unsuccessful response
                            // You can retrieve the error code and message if needed
                            int statusCode = response.code();
                            String errorMessage = response.message();
                        }
                    }
                });

                }
        });
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadJSON();

                Intent intent = new Intent(getApplicationContext(), Download_Order_Details.class);
                startActivity(intent);
            }
        });
    }

    private void downloadJSON() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(DOWNLOAD_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Home.this, "Download failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {

                    final String json = response.body().string();



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Home.this, "Download completed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Home.this, "Download failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}