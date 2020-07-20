package com.example.saferestaurants;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Problem!";
    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean permissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Restaurants restaurants = Restaurants.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //verify permissions
        getLocationAccess();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        //if(isRestaurantsEmpty())
        //setData();
    }


    /**
     * Manipulates the map once available.ve the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or mo
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(49.2797519, -122.96552349999997);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Current location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (permissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
        //Display pegs for restaurants in out list
        //displayRestaurantPegs();
    }

    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(permissionGranted) {
                Task place = fusedLocationProviderClient.getLastLocation();
                place.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            //found the location
                            Location currentL = (Location) task.getResult();
                            moveCamera(new LatLng(currentL.getLatitude(), currentL.getLongitude()), 15f);
                        } else {
                            //Didn't find location
                            Toast.makeText(MapsActivity.this, "Cant get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e(TAG, e.getMessage());
        }
    }

    // move the camera view to the given location
    private void moveCamera (LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    //verify permissions
    private void getLocationAccess(){
        String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
                mapINIT();
            } else{
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            }
        } else{
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionGranted = false;
        switch(requestCode){
            case PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                    //setUp map
                    mapINIT();
                }
            }
        }
    }

    private void mapINIT(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    private boolean isRestaurantsEmpty() {
        return restaurants.size() == 0;
    }

    private void setData() {

        // Setting up Restaurants Class Data //
        FileInputStream inputStreamRestaurants = null;
        try {
            File file = new File(getFilesDir().toString() + "/" + "restaurants_itr2.csv");
            for (String filee : getFilesDir().list()) {
                System.out.println(filee);
            }

            inputStreamRestaurants = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // InputStream inputStreamRestaurants = getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader readerRestaurants = new BufferedReader(
                new InputStreamReader(inputStreamRestaurants, Charset.forName("UTF-8"))
        );
        DataParser.parseRestaurants(readerRestaurants);
        //                                  //

        // Setting up Inspections Data for each Restaurant //
        InputStream inputStreamInspections = null;
        try {
            File file = new File(getFilesDir().toString() + "/" + "inspectionreports_itr2.csv");
            for (String filee : getFilesDir().list()) {
                System.out.println(filee);
            }

            inputStreamInspections = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //InputStream inputStreamInspections = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader readerInspections = new BufferedReader(
                new InputStreamReader(inputStreamInspections, Charset.forName("UTF-8"))
        );
        DataParser.parseInspections(readerInspections);
        //                                                //
    }

    public void displayRestaurantPegs(){
        for(int i = 0; i < restaurants.size(); i++){
            Restaurant currentRestaurant = restaurants.get(i);
            LatLng restaurantGPS = new LatLng(currentRestaurant.getLatitude(), currentRestaurant.getLongitude());

            //String restaurantHazardLevel = "SHIT";

            mMap.addMarker(new MarkerOptions()
                    .position(restaurantGPS)
                    .title(currentRestaurant.getName())
            );
        }
    }
}
