package com.example.newspaperdeliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import ai.nextbillion.maps.Nextbillion;
import ai.nextbillion.maps.core.MapView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import ai.nextbillion.kits.geojson.Point;
import ai.nextbillion.maps.Nextbillion;
import ai.nextbillion.maps.camera.CameraUpdate;
import ai.nextbillion.maps.camera.CameraUpdateFactory;
import ai.nextbillion.maps.core.MapView;
import ai.nextbillion.maps.core.NextbillionMap;
import ai.nextbillion.maps.core.OnMapReadyCallback;
import ai.nextbillion.maps.core.Style;
import ai.nextbillion.maps.geometry.LatLng;
import ai.nextbillion.maps.location.engine.LocationEngine;
import ai.nextbillion.maps.location.engine.LocationEngineCallback;
import ai.nextbillion.maps.location.engine.LocationEngineProvider;
import ai.nextbillion.maps.location.engine.LocationEngineRequest;
import ai.nextbillion.maps.location.engine.LocationEngineResult;
public class Customer extends AppCompatActivity {
Spinner newspaper,Subscription;
EditText copi;
MapView mapView;
Button order,Logout;
String URL = "http://192.168.1.168:8080/Newspaper_delivery_App/order.php";
String latitude, longitude,selectednewspaper,selectedSubscription,copy,Status;
    final long UPDATE_INTERVAL_IN_MILLISECONDS = 50000;
    LocationEngine locationEngine;

    NextbillionMap nextbillionMap;

    final NavigationLauncherLocationCallback callbackL =
            new NavigationLauncherLocationCallback(this);

    Point origin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Nextbillion.getInstance(getApplicationContext(),"b98e9dd2f9414231bae19340b76feff0");
        setContentView(R.layout.activity_customer);
        origin = Point.fromLngLat(79.955160, 6.903827);
        newspaper = (Spinner)findViewById(R.id.dropdown);
        Subscription = (Spinner)findViewById(R.id.Subscription);
        copi = (EditText)findViewById(R.id.txtcopi);
        mapView = findViewById(R.id.mapView);
        order = (Button)findViewById(R.id.btnOrder);

         selectednewspaper = newspaper.getSelectedItem().toString();
         selectedSubscription = Subscription.getSelectedItem().toString();


        Status = "Pending";
        Logout = findViewById(R.id.BtnLogout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!latitude.equals("") && !longitude.equals("")){

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                Toast.makeText(Customer.this, "Order Successfully", Toast.LENGTH_SHORT).show();

                                order.setClickable(false);
                            } else if (response.equals("failure")) {
                                Toast.makeText(Customer.this, "Order Unsuccessfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            copy = copi.getText().toString();
                            Map<String, String> data = new HashMap<>();
                            data.put("newspaperName", selectednewspaper);
                            data.put("SubscriptionPlane", selectedSubscription);
                            data.put("Copies", copy);
                            data.put("Status", Status);
                            data.put("longitude", longitude);
                            data.put("latitude", latitude);
                            return data;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }
            }

        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull NextbillionMap nextbillionMap) {
                Customer.this.nextbillionMap = nextbillionMap;
                String styleUri = "https://api.nextbillion.io/maps/streets/style.json?key="
                        + Nextbillion.getAccessKey();
                nextbillionMap.setStyle(new Style.Builder().fromUri(styleUri));
                nextbillionMap.getStyle(new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        initializeLocationEngine();
                        animateCamera(new LatLng(origin.latitude(), origin.longitude()));
                        nextbillionMap.addOnMapLongClickListener(new NextbillionMap.OnMapLongClickListener() {
                            @Override
                            public boolean onMapLongClick(@NonNull LatLng latLng) {
                                if(nextbillionMap.getMarkers().size() > 0){
                                    nextbillionMap.removeMarker(nextbillionMap.getMarkers().get(0));
                                }
                                nextbillionMap.addMarker(latLng);
                                latitude = String.valueOf(latLng.getLatitude());
                                longitude = String.valueOf(latLng.getLongitude());
                                return false;
                            }
                        });
                    }
                });
            }
        });
    }

    @NonNull
    private LocationEngineRequest buildEngineRequest() {
        return new LocationEngineRequest.Builder(UPDATE_INTERVAL_IN_MILLISECONDS).
                setFastestInterval(UPDATE_INTERVAL_IN_MILLISECONDS).
                setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY).build();
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        LocationEngineRequest request = buildEngineRequest();
        locationEngine.requestLocationUpdates(request, callbackL, null);
        locationEngine.getLastLocation(callbackL);
    }



    private static class NavigationLauncherLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<Customer> activityWeakReference;

        NavigationLauncherLocationCallback(Customer activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            Customer activity = activityWeakReference.get();
            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
                activity.onLocationFound(location);
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            exception.printStackTrace();
        }
    }

    void onLocationFound(Location location) {
        animateCamera(new LatLng(location.getLatitude(), location.getLongitude()));
    }


    private void animateCamera(LatLng point) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 10);
        nextbillionMap.easeCamera(cameraUpdate);
    }


}