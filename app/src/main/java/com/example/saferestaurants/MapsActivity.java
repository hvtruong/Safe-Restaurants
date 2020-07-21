package com.example.saferestaurants;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.saferestaurants.model.ClusterMarker;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;
import com.example.saferestaurants.util.ClusterManagerRenderer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Inspections;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;
import com.google.maps.android.clustering.ClusterManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Problem!";
    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean permissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Restaurants restaurants = Restaurants.getInstance();
    private static ProgressDialog loadingAlert;
    private ClusterManager<ClusterMarker> clusterManager;
    private ClusterManagerRenderer clusterManagerRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //verify permissions
        getLocationAccess();
        setUpToggleButton();

        long time = System.currentTimeMillis();
        if (isUpdateTime(time) && isRestaurantsEmpty()) {
            showUpdatePopUp(time);
        }

        if (isRestaurantsEmpty()) {
            setData();
        }

    }

    class RetrieveData extends AsyncTask<Void, Void, Void> {
        // Asynchronously fetch inspection data.
        DataFetcher dataFetcher = new DataFetcher();

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isCancelled()){
                dataFetcher.fetchData(DataFetcher.restaurantDatabaseURL);
            }

            if(!isCancelled()){
                dataFetcher.fetchData(DataFetcher.inspectionDatabaseURL);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            loadingAlert = new ProgressDialog(MapsActivity.this);
            loadingAlert.setMessage("Updating data, Please wait..");
            loadingAlert.setCancelable(false);
            loadingAlert.setButton(
                    DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingAlert.dismiss();
                            RetrieveData.this.cancel(true);
                        }
                    }
            );
            loadingAlert.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            restaurants = Restaurants.newInstance();
            saveTime(System.currentTimeMillis());
            try {
                copyFile(new File(getFilesDir().toString() + "/" + DataFetcher.restaurantFileName),
                        new File(getFilesDir().toString() + "/" + "restaurants_itr2.csv"));
                copyFile(new File(getFilesDir().toString() + "/" + DataFetcher.inspectionFileName),
                        new File(getFilesDir().toString() + "/" + "inspectionreports_itr2.csv"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            setData();
            loadingAlert.dismiss();

        }
    }

    //         // new stuff for time //         //
    private boolean isUpdateTime(long currentTime){
        long time = loadTime();
        return currentTime - time >= 7.2E7;
    }
    private void saveTime(long time){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(time);
        editor.putString("Time", json);
        editor.apply();
    }
    private long loadTime(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Time", String.valueOf(0));
        Type type = new TypeToken<Long>() {}.getType();
        return (Long) gson.fromJson(json, type);
    }
    //              //              //              //

//  ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   //

    //              //              //              //
    private void showUpdatePopUp(final long time){

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("Do you want to update your restaurant data?");
        builder.setTitle("Update Available");
        builder.setCancelable(false);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                DataFetcher.setFileLocation(getFilesDir().toString());
                new MapsActivity.RetrieveData().execute();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    //              //              //              //

    // Source: https://stackoverflow.com/questions/29867121/how-to-copy-programmatically-a-file-to-another-directory
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
    //          //          //          //          //          //          //          //          //

    private void setInitialData(){
        // Setting up Restaurants Class Data //
        InputStream inputStreamRestaurants = getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader readerRestaurants = new BufferedReader(
                new InputStreamReader(inputStreamRestaurants, Charset.forName("UTF-8"))
        );
        DataParser.parseRestaurantsIteration1(readerRestaurants);
        //                                  //

        // Setting up Inspections Data for each Restaurant //
        InputStream inputStreamInspections = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader readerInspections = new BufferedReader(
                new InputStreamReader(inputStreamInspections, Charset.forName("UTF-8"))
        );
        DataParser.parseInspectionsIteration1(readerInspections);
        //                                                //

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
            BufferedReader readerRestaurants = new BufferedReader(
                    new InputStreamReader(inputStreamRestaurants, Charset.forName("UTF-8"))
            );
            DataParser.parseRestaurants(readerRestaurants);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FileInputStream inputStreamInspections = null;
        try {
            File file = new File(getFilesDir().toString() + "/" + "inspectionreports_itr2.csv");
            for (String filee : getFilesDir().list()) {
                System.out.println(filee);
            }

            inputStreamInspections = new FileInputStream(file);
            BufferedReader readerInspections = new BufferedReader(
                    new InputStreamReader(inputStreamInspections, Charset.forName("UTF-8"))
            );
            DataParser.parseInspections(readerInspections);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(isRestaurantsEmpty()){
            setInitialData();
        }
    }

    private void setUpToggleButton() {
        Button btn = (Button) findViewById(R.id.listToggle);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //finish
            }
        });
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
        if (permissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
        //Display and cluster pegs for restaurants in out list
        displayRestaurantPegs();
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

    public void displayRestaurantPegs(){
        if(clusterManager == null) {
            clusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(), mMap);
            mMap.setOnCameraIdleListener(clusterManager);
            mMap.setOnMarkerClickListener(clusterManager);
            mMap.setOnInfoWindowClickListener(clusterManager);
        }
        if(clusterManagerRenderer == null){
            clusterManagerRenderer = new ClusterManagerRenderer(
                    MapsActivity.this,
                    mMap,
                    clusterManager
            );
            clusterManager.setRenderer(clusterManagerRenderer);
        }
        for(int i = 0; i < restaurants.size(); i++){
            Restaurant currentRestaurant = restaurants.get(i);
            LatLng restaurantGPS = new LatLng(currentRestaurant.getLatitude(), currentRestaurant.getLongitude());
            String hazardLevel;
            if(currentRestaurant.getInspection().size()!= 0){
                hazardLevel = currentRestaurant.getInspection().get(0).getHazardRating();
            }
            else{
                hazardLevel = ("Low");
            }
            int hazardIcon;
            if(hazardLevel.equals("Low")){
                hazardIcon = R.drawable.low_hazard_24dp;
            }
            else if (hazardLevel.equals("Moderate")){
                hazardIcon = R.drawable.moderate_hazard_24;
            }
            else{
                hazardIcon = R.drawable.high_hazard_24dp;
            }
            ClusterMarker newClusterMarker = new ClusterMarker(
                    restaurantGPS,
                    currentRestaurant.getName(),
                    currentRestaurant.getPhysicalAddress(),
                    hazardIcon
            );
            clusterManager.addItem(newClusterMarker);

            if(currentRestaurant.getInspection().size()!= 0){
                hazardLevel = currentRestaurant.getInspection().get(0).getHazardRating();
            }
            else{
                hazardLevel = ("Low");
            }

            /*if(hazardLevel.equals(getString(R.string.moderate))){
                mMap.addMarker(new MarkerOptions()
                        .position(restaurantGPS)
                        .title(currentRestaurant.getName())
                        .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.moderate_hazard_24))
                        .snippet(currentRestaurant.getPhysicalAddress() + getString(R.string.end_line) + getString(R.string.Hazard_level) + hazardLevel)
                );
            }
            else if(hazardLevel.equals(getString(R.string.low))){
                mMap.addMarker(new MarkerOptions()
                        .position(restaurantGPS)
                        .title(currentRestaurant.getName())
                        .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.low_hazard_24dp))
                        .snippet(currentRestaurant.getPhysicalAddress() + getString(R.string.end_line) + getString(R.string.Hazard_level) + hazardLevel)
                );
            }
            else{
                mMap.addMarker(new MarkerOptions()
                        .position(restaurantGPS)
                        .title(currentRestaurant.getName())
                        .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.high_hazard_24dp))
                        .snippet(currentRestaurant.getPhysicalAddress() + getString(R.string.end_line) + getString(R.string.Hazard_level) + hazardLevel)
                );
            }*/
        }
        clusterManager.cluster();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
