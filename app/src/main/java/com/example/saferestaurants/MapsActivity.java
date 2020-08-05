//This class is to shows the map and pegs for all restaurants we have within the local data
package com.example.saferestaurants;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.saferestaurants.model.ClusterMarker;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;
import com.example.saferestaurants.util.ClusterManagerRenderer;
import com.example.saferestaurants.util.CustomMarkerInfoWindow;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.nio.charset.StandardCharsets;
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
    private ArrayList<ClusterMarker> collectionOfMarker = new ArrayList<>();
    private int returnedRestaurantID = -1;
    private EditText search_Content;
    private String searchContent;
    private static final String SHARED_PREF = "sharedPrefs";
    private static final String numOfCrit = "searchedNumber";
    private static final String inequality = "inequality";
    private static final String specificHazardLevel = "hazardLevel";
    private static final String onlyFavorite = "favorite";
    private String inequalityExtracted = "";
    private int numberOfCriticalIssuesExtracted = -1;
    private String hazardLevelExtracted = "";
    private boolean displayOnlyFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Extract search content from search bar
        search_Content = findViewById(R.id.search_content);
        searchContent = "";

        //Goes to Filter Option screen if users want
        Button filterOption = findViewById(R.id.Filter);
        filterOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, FilterOption.class);
                startActivity(intent);
            }
        });

        //verify permissions
        getLocationAccess();
        setUpToggleButton();

        long time = System.currentTimeMillis();
        if (isUpdateTime(time) && isRestaurantsEmpty()) {
            new checkServer().execute();
        }

        if (isRestaurantsEmpty()) {
            setData();
        }
    }

    //Update data functions:
    //          //          //          //          //          //          //          //          //
    class RetrieveData extends AsyncTask<Void, Void, Void> {
        // Asynchronously fetch inspection data.
        DataFetcher dataFetcher = new DataFetcher();

        @Override
        protected Void doInBackground(Void... voids) {
            saveURL(dataFetcher.fetchData(DataFetcher.restaurantDatabaseURL), "URL Restaurants");
            saveURL(dataFetcher.fetchData(DataFetcher.inspectionDatabaseURL), "URL Inspections");
            publishProgress();
            return null;
        }

        @Override
        protected void onPreExecute() {
            loadingAlert = new ProgressDialog(MapsActivity.this);
            loadingAlert.setMessage(getString(R.string.updating));
            loadingAlert.setCancelable(false);
            loadingAlert.setButton(
                    DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancelButton), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RetrieveData.this.cancel(true);
                            loadingAlert.dismiss();
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

        @Override
        protected void onProgressUpdate(Void... values) {
            loadingAlert.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
            loadingAlert.setMessage(getString(R.string.setting));
        }
    }

    class checkServer extends AsyncTask<Void, Void, Void> {
        public String URLRestaurant;
        public String URLInspection;

        @Override
        protected Void doInBackground(Void... voids) {
            URLRestaurant = DataFetcher.fetchDataURL(DataFetcher.restaurantDatabaseURL);
            URLInspection = DataFetcher.fetchDataURL(DataFetcher.inspectionDatabaseURL);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(loadURL("URL Restaurants").length() == 0){
                showUpdatePopUp();
            }
            if(loadURL("URL Inspections").length() == 0){
                showUpdatePopUp();
            }
            if(!loadURL("URL Restaurants").equals(URLRestaurant)){
                showUpdatePopUp();
            } else if(!loadURL("URL Inspections").equals(URLInspection)){
                showUpdatePopUp();
            }
        }
    }

    //         // stuff for time //         //
    private boolean isUpdateTime(long currentTime){
        long time = loadTime();
        return (currentTime - time >= 7.2E7);
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

    private boolean isRestaurantsEmpty() {
        return restaurants.size() == 0;
    }
    private String loadURL(String URLType){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(URLType, String.valueOf(0));
        Type type = new TypeToken<String>() {}.getType();
        return (String)gson.fromJson(json, type);
    }
    private void saveURL(String URL, String URLType){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(URL);
        editor.putString(URLType, json);
        editor.apply();
    }
//  ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   ~   //

    //              //              //              //
    private void showUpdatePopUp(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage(getString(R.string.updatePrompt));
        builder.setTitle(getString(R.string.updateTitle));
        builder.setCancelable(false);

        builder.setPositiveButton(getString(R.string.updateButton), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                DataFetcher.setFileLocation(getFilesDir().toString());
                new MapsActivity.RetrieveData().execute();
            }
        });

        builder.setNegativeButton(getString(R.string.cancelButton), new DialogInterface.OnClickListener() {
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
                new InputStreamReader(inputStreamRestaurants, StandardCharsets.UTF_8)
        );
        DataParser.parseRestaurantsIteration1(readerRestaurants);
        //                                  //

        // Setting up Inspections Data for each Restaurant //
        InputStream inputStreamInspections = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader readerInspections = new BufferedReader(
                new InputStreamReader(inputStreamInspections, StandardCharsets.UTF_8)
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
                    new InputStreamReader(inputStreamRestaurants, StandardCharsets.UTF_8)
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
                    new InputStreamReader(inputStreamInspections, StandardCharsets.UTF_8)
            );
            DataParser.parseInspections(readerInspections);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(isRestaurantsEmpty()){
            setInitialData();
        }
    }
    //          //          //          //          //          //          //          //          //
    // End of Update data functions


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

        if(RestaurantDetail.gpsClicked){
            extractGPSInfoWindow();
        }

        //Display and cluster pegs for restaurants in out list
        displayRestaurantPegs(searchContent);
        
        init();

    }

    // Map display functions:
    //          //          //          //          //          //          //          //          //
    private void init(){
        search_Content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    searchContent = search_Content.getText().toString();
                    displayRestaurantPegs(searchContent);
                }
                return false;
            }
        });
    }

    private void extractGPSInfoWindow(){
        returnedRestaurantID = getIntent().getIntExtra("restaurantID",-1);
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
                            moveCamera(new LatLng(currentL.getLatitude(), currentL.getLongitude()), 18f);
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
        if(RestaurantDetail.gpsClicked){
            LatLng rData = new LatLng(RestaurantDetail.selectedLad, RestaurantDetail.selectedLong);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rData,zoom));
            //make pin info appear here
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
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

    public void displayRestaurantPegs(String searchedContent){

        if(FilterOption.filterSaved){
            extractSearchCriteria();
        }

        //Initialize clusterManager
        if(clusterManager != null) {
            clusterManager.clearItems();
            mMap.clear();
        }
        else{
            clusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(), mMap);
        }

        //Initialize clusterManagerRenderer
        if(clusterManagerRenderer == null){
            clusterManagerRenderer = new ClusterManagerRenderer(
                    MapsActivity.this,
                    mMap,
                    clusterManager,
                    returnedRestaurantID
            );
            clusterManager.setRenderer(clusterManagerRenderer);
        }

        //Set up for cluster
        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        clusterManager.getMarkerCollection().setInfoWindowAdapter(new CustomMarkerInfoWindow(MapsActivity.this));
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        mMap.setOnInfoWindowClickListener(clusterManager);

        //Display pegs for restaurants
        for(int i = 0; i < restaurants.size(); i++)
        {
            Restaurant currentRestaurant = restaurants.get(i);
            String restaurantName = currentRestaurant.getName();
            String lowerCaseRestaurantName = currentRestaurant.getName().toLowerCase();
            if((restaurantName.contains(searchedContent) || lowerCaseRestaurantName.contains(searchedContent)) &&
                    hasTheSpecificHazardLevel(currentRestaurant) &&
                    satisfyNumOfCriticalIssuesInequality(currentRestaurant) &&
                    (!displayOnlyFavorite || isFavoriteRestaurant(currentRestaurant))
                )
            {
            LatLng restaurantGPS = new LatLng(currentRestaurant.getLatitude(), currentRestaurant.getLongitude());

            //Extract hazard color of restaurants
            String hazardLevel;
            if (currentRestaurant.getInspection().size() != 0) {
                hazardLevel = currentRestaurant.getInspection().get(0).getHazardRating();
            } else {
                hazardLevel = ("Low");
            }

            //Assign hazard color for pegs
            int hazardIcon;
            if (hazardLevel.equals("Low")) {
                hazardIcon = R.drawable.low_hazard_24dp;
            } else if (hazardLevel.equals("Moderate")) {
                hazardIcon = R.drawable.moderate_hazard_24;
            } else {
                hazardIcon = R.drawable.high_hazard_24dp;
            }

            //Add new ClusterMarker
            ClusterMarker newClusterMarker = new ClusterMarker(
                    restaurantGPS,
                    currentRestaurant.getName(),
                    currentRestaurant.getPhysicalAddress() + getString(R.string.end_line) +
                            getString(R.string.Hazard_level) + hazardLevel,
                    hazardIcon,
                    i
            );
            clusterManager.addItem(newClusterMarker);
            collectionOfMarker.add(newClusterMarker);

            //Go to detail if Info Window clicked
            clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>() {
                @Override
                public void onClusterItemInfoWindowClick(ClusterMarker item) {
                    Intent intent = RestaurantDetail.makeIntent(MapsActivity.this, item.getRestaurantID());
                    startActivity(intent);
                }
            });
            }
        }
        clusterManager.cluster();

    }
    //          //          //          //          //          //          //          //          //
    // End of Map display functions

    //Toggle to List
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

    private void extractSearchCriteria(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        inequalityExtracted = sharedPreferences.getString(inequality, "");
        numberOfCriticalIssuesExtracted = sharedPreferences.getInt(numOfCrit,-1);
        hazardLevelExtracted = sharedPreferences.getString(specificHazardLevel,"");
        displayOnlyFavorite = sharedPreferences.getBoolean(onlyFavorite, false);
    }

    private boolean hasTheSpecificHazardLevel(Restaurant restaurant){
        if(hazardLevelExtracted.equals("") ||
                (restaurant.getInspection().size() != 0 &&
                        (hazardLevelExtracted.equals(restaurant.getInspection().get(0).getHazardRating()) ||
                        hazardLevelExtracted.equals(restaurant.getInspection().get(0).getHazardRating().toLowerCase())))
        ){
            return true;
        }
        return false;
    }

    private boolean satisfyNumOfCriticalIssuesInequality(Restaurant restaurant){
        if(numberOfCriticalIssuesExtracted == -1 ||
        inequalityExtracted.equals("") ||
                (inequalityExtracted.equals("less than or equal to") &&
                        restaurant.getInspection().totalNumberOfCriticalIssuesLastYear() <= numberOfCriticalIssuesExtracted) ||
                (inequalityExtracted.equals("greater than or equal to") &&
                        restaurant.getInspection().totalNumberOfCriticalIssuesLastYear() >= numberOfCriticalIssuesExtracted)
        ){
            return true;
        }
        return false;
    }

    private boolean isFavoriteRestaurant(Restaurant restaurant){
        if(displayOnlyFavorite){
            //Add condition for favorite restaurants later
            return true;
        }
        return false;
    }
}
