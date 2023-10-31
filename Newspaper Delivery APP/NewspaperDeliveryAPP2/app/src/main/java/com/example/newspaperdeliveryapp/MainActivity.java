package com.example.newspaperdeliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
public class MainActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private RadioButton Customer,Distributor;
    private String email, password;
    private String URL = "http://192.168.1.168:8080/Newspaper_delivery_App/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = password = "";
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        Customer = (RadioButton) findViewById(R.id.RbCustomer);
        Distributor = (RadioButton) findViewById(R.id.RbDistributor);

    }

    public void Register(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);

    }

    public void Login(View view) {
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if(!email.equals("") && !password.equals("")){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("res", response);
                    if (response.equals("success")) {

                        if(Customer.isChecked()){
                            Intent intent = new Intent(MainActivity.this, Customer
                                    .class);
                            startActivity(intent);
                            finish();
                        }
                        if(Distributor.isChecked()){
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            startActivity(intent);
                            finish();
                        }

                    } else if (response.equals("failure")) {
                        Toast.makeText(MainActivity.this, "Invalid Login Id/Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("email", email);
                    data.put("password", password);
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }else{
            Toast.makeText(this, "Fields can not be empty!", Toast.LENGTH_SHORT).show();
        }
    }

}