package com.example.saferestaurants;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Restaurants restaurants= Restaurants.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(isRestaurantsEmpty())
            setData();
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
        LatLng sydney = new LatLng(49.2797519, -122.96552349999997);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //Display pegs for restaurants in out list
        displayRestaurantPegs();
    }

    private boolean isRestaurantsEmpty() {
        return restaurants.size() == 0;
    }

    private void setData() {

        // Setting up Restaurants Class Data //
        InputStream inputStreamRestaurants = getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader readerRestaurants = new BufferedReader(
                new InputStreamReader(inputStreamRestaurants, Charset.forName("UTF-8"))
        );
        DataParser.parseRestaurants(readerRestaurants);
        //                                  //

        // Setting up Inspections Data for each Restaurant //
        InputStream inputStreamInspections = getResources().openRawResource(R.raw.inspectionreports_itr1);
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
            mMap.addMarker(new MarkerOptions()
                    .position(restaurantGPS)
                    .title(currentRestaurant.getName())
            );
        }
    }
}
