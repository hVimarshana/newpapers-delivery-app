package com.example.newspaperdeliveryapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

import ai.nextbillion.kits.directions.models.DirectionsResponse;
import ai.nextbillion.kits.directions.models.DirectionsRoute;
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
import ai.nextbillion.maps.location.modes.RenderMode;
import ai.nextbillion.navigation.ui.NBNavigation;
import ai.nextbillion.navigation.ui.NavLauncherConfig;
import ai.nextbillion.navigation.ui.NavigationLauncher;
import ai.nextbillion.navigation.ui.camera.CameraUpdateMode;
import ai.nextbillion.navigation.ui.camera.NavigationCameraUpdate;
import ai.nextbillion.navigation.ui.map.NavNextbillionMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LOCATION extends AppCompatActivity {

    final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 500;
    private static final int DEFAULT_CAMERA_ZOOM = 11;
    private static final int CAMERA_ANIMATION_DURATION = 1000;
    LocationEngine locationEngine;
    private static NavNextbillionMap navNextbillionMap;
    private static boolean locationFound;
    Point currentLocation;
    MapView mapView;
    DirectionsRoute route;
    Point origin, destination;

    final NavigationLauncherLocationCallback callbackL =
            new NavigationLauncherLocationCallback(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Nextbillion.getInstance(getApplicationContext(), "b98e9dd2f9414231bae19340b76feff0");
        setContentView(R.layout.activity_location);
        origin = Point.fromLngLat(79.955160, 6.903827);
        Bundle bundle = getIntent().getExtras();
        String lat = bundle.getString("lat");
        String lon = bundle.getString("long");
        destination = Point.fromLngLat(Double.parseDouble(lon), Double.parseDouble(lat));
        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        Button button = findViewById(R.id.btnFinished);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }


        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull NextbillionMap nextbillionMap) {
                String styleUri = "https://api.nextbillion.io/maps/streets/style.json?key="
                        + Nextbillion.getAccessKey();
                nextbillionMap.setStyle(new Style.Builder().fromUri(styleUri));
                nextbillionMap.getStyle(new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        navNextbillionMap = new NavNextbillionMap(mapView, nextbillionMap);
                        navNextbillionMap.updateLocationLayerRenderMode(RenderMode.COMPASS);
                        initializeLocationEngine();
                        animateCamera(new LatLng(origin.latitude(), origin.longitude()));
                        navNextbillionMap.addMarker(getApplicationContext(), destination);
                        fetchRoute();
                    }
                });
            }
        });

    }

    public void fetchRoute(){
        NBNavigation.fetchRoute(origin, destination, new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call,
                                   Response<DirectionsResponse> response) {
                if (response.isSuccessful()){
                    DirectionsResponse directionsResponse = response.body();
                    DirectionsRoute route = directionsResponse.routes().get(0);
                    if (route.distance() > 25d){
                        LOCATION.this.route = route;
                        navNextbillionMap.drawRoute(route);
                    }
                    else
                        Snackbar.make(mapView, "Select longer route", Snackbar.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

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

        private final WeakReference<LOCATION> activityWeakReference;

        NavigationLauncherLocationCallback(LOCATION activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            LOCATION activity = activityWeakReference.get();
            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
                activity.updateCurrentLocation(Point.fromLngLat(location.getLongitude(), location.getLatitude()));
                activity.onLocationFound(location);
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            exception.printStackTrace();
        }
    }

    void updateCurrentLocation(Point currentLocation) {
        this.currentLocation = currentLocation;
    }

    void onLocationFound(Location location) {
        navNextbillionMap.updateLocation(location);
        if (!locationFound) {
            animateCamera(new LatLng(location.getLatitude(), location.getLongitude()));
            locationFound = true;
        }
    }

    private static void animateCamera(LatLng point) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, DEFAULT_CAMERA_ZOOM);
        NavigationCameraUpdate navigationCameraUpdate = new NavigationCameraUpdate(cameraUpdate);
        navigationCameraUpdate.setMode(CameraUpdateMode.OVERRIDE);
        navNextbillionMap.retrieveCamera().update(navigationCameraUpdate, CAMERA_ANIMATION_DURATION);
    }

}