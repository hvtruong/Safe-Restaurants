package com.example.saferestaurants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/*
This class is displaying the details about a single restaurant including:
 -name
 -physical address
 -GPS coordinates
 -a list of inspections that happened to this restaurant.
 */
public class RestaurantDetail extends AppCompatActivity {
    Restaurants restaurants = Restaurants.getInstance();
    Restaurant restaurant;
    int restaurantID;
    ListView inspectionListView;
    private static final String SHARED_PREF = "sharedPrefs";
    private static final String reservedRestaurantID = "restaurantID";
    public static double selectedLad;
    public static double selectedLong;
    public static boolean gpsClicked = false;
    public static Restaurant restaurantPicked;
    private static ArrayList<Restaurant> favList = new ArrayList<Restaurant>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        Toolbar toolbar = findViewById(R.id.singleRestaurantToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        extractRestaurant();

        setUpTitle();
        setUpListView();
        GpsClickSetUp();
        setUpFavButtonText();
        setUpFavButton();
    }

    private void setUpFavButtonText() {
        Button button = findViewById(R.id.fav);
        if(!restaurant.isFavorite()){
            button.setText(R.string.mark_favorite);
        } else{
            button.setText(R.string.unmark_favorite);
        }
        SharedPreferences prefs = getSharedPreferences("favList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("favList", null);
        Type type = new TypeToken<ArrayList<Restaurant>>() {}.getType();
        favList = gson.fromJson(json, type);
        if(favList == null){
            favList = new ArrayList<Restaurant>();
        }
    }

    private void setUpFavButton() {
        final Button button = findViewById(R.id.fav);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("clicked!");
                if(restaurant.isFavorite()){
                    if(favList != null) {
                        for (int i = 0; i < favList.size(); i++) {
                            if (favList.get(i).getName().equals(restaurant.getName()) && favList.get(i).getPhysicalAddress().equals(restaurant.getPhysicalAddress())) {
                                favList.remove(i);
                                SharedPreferences prefs = getSharedPreferences("favList", MODE_PRIVATE);
                                SharedPreferences.Editor prefEdit = prefs.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(favList);
                                prefEdit.putString("favList", json);
                                prefEdit.apply();
                                System.out.println("Not a fav!");
                                break;
                            }
                        }
                    }

                    //update list view activity
                    button.setText(R.string.mark_favorite);
                    restaurant.setFavorite(false);
                } else{
                    favList.add(restaurant);
                    SharedPreferences prefs = getSharedPreferences("favList", MODE_PRIVATE);
                    SharedPreferences.Editor prefEdit = prefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(favList);
                    prefEdit.putString("favList", json);
                    prefEdit.apply();
                    //update list view activity
                    button.setText(R.string.unmark_favorite);
                    restaurant.setFavorite(true);
                    System.out.println("now a fav!");
                }
            }
        });

    }

    //Go to Map by clicking on GPS
    private void GpsClickSetUp() {
        TextView textView = findViewById(R.id.restaurantGPS);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedLad = restaurant.getLatitude();
                selectedLong = restaurant.getLongitude();
                gpsClicked = true;
                restaurantPicked = restaurant;
                Intent intent = new Intent(RestaurantDetail.this, MapsActivity.class);
                intent.putExtra("restaurantID",restaurantID);
                startActivity(intent);
            }
        });

    }

    //Return intent to MainActivity
    public static Intent makeIntent(Context context, int restaurantID){
        Intent intent = new Intent(context, RestaurantDetail.class);
        intent.putExtra("restaurantID",restaurantID);
        return intent;
    }

    //Extract the chosen restaurant
    public void extractRestaurant(){
        Intent intent = getIntent();
        restaurantID = intent.getIntExtra("restaurantID",-1);
        if(restaurantID == -1){
            getReservedRestaurantID();
        }
        restaurant = restaurants.get(restaurantID);
    }

    public void getReservedRestaurantID(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        this.restaurantID = sharedPreferences.getInt(reservedRestaurantID, 0);
    }

    //Display Name, Address and GPS coordinates of the restaurant
    public void setUpTitle(){
        TextView textView = findViewById(R.id.restaurantName);
        TextView textView1 = findViewById(R.id.restaurantAddress);
        TextView textView2 = findViewById(R.id.restaurantGPS);

        textView.setText(restaurant.getName());
        textView1.setText(getString(R.string.address) + restaurant.getPhysicalAddress());
        textView2.setText(getString(R.string.gps) + restaurant.getLatitude() + "\t \t" + restaurant.getLongitude());
    }

    //Display the list of inspections of the restaurant
    public void setUpListView(){
        ArrayAdapter<Inspection> inspectionArrayAdapter = new inspectionAdapter();
        inspectionListView = findViewById(R.id.inspectionListView);
        inspectionListView.setAdapter(inspectionArrayAdapter);
    }

    private class inspectionAdapter extends ArrayAdapter<Inspection>{

        public inspectionAdapter(){
            super(RestaurantDetail.this,R.layout.inspection_item_view, restaurant.getInspection().getInspections());
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Make sure view is not null
            View itemView = convertView;
            if(itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.inspection_item_view,parent,false);

            Inspection currentInspection = restaurant.getInspection().get(position);

            //Set up hazard icons and colors for items of list view
            ImageView hazardIcon = itemView.findViewById(R.id.item_icon);

            if(isLowHazard(currentInspection)) {
                hazardIcon.setImageResource(R.drawable.low_hazard);
                itemView.setBackgroundColor(Color.parseColor("#9090FF81"));
            }
            else if(isModerateHazard(currentInspection)) {
                hazardIcon.setImageResource(R.drawable.moderate_hazard);
                itemView.setBackgroundColor(Color.parseColor("#88FFD372"));
            }
            else{
                hazardIcon.setImageResource(R.drawable.high_hazard);
                itemView.setBackgroundColor(Color.parseColor("#83FF8273"));
            }

            //Displaying summary of inspection information
            TextView inspectionDate = itemView.findViewById(R.id.inspectionDate);
            inspectionDate.setText(inspectionTime(currentInspection));

            TextView inspectionCriticalIssues = itemView.findViewById(R.id.inspectionNumberOfCriticalIssues);
            inspectionCriticalIssues.setText(getStringCriticalIssues(currentInspection));

            TextView inspectionNonCriticalIssues = itemView.findViewById(R.id.inspectionNumberOfNonCriticalIssues);
            inspectionNonCriticalIssues.setText(getStringNonCriticalIssues(currentInspection));

            //Passing chosen inspection to the next Acitivity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent singleInspection = SingleInspection.makeIntent(RestaurantDetail.this, restaurantID, position);
                    startActivity(singleInspection);
                }
            });

            return itemView;
        }
    }

    private String getStringNonCriticalIssues(Inspection inspection){
        int numberOfNonCriticalIssues = inspection.getNonCriticalIssues();
        if(numberOfNonCriticalIssues == 0)
            return getString(R.string.no_non_critical_issue_found);
        else if(numberOfNonCriticalIssues == 1)
            return getString(R.string.textOffSet) + numberOfNonCriticalIssues + getString(R.string.non_critical_issue_found);
        else
            return getString(R.string.textOffSet) + numberOfNonCriticalIssues + getString(R.string.non_critical_issues_found);
    }

    private String getStringCriticalIssues(Inspection inspection){
        int numberOfCriticalIssues = inspection.getCriticalIssues();
        if(numberOfCriticalIssues == 0)
            return getString(R.string.no_critical_issue_found);
        else if(numberOfCriticalIssues == 1)
            return getString(R.string.textOffSet) + numberOfCriticalIssues + getString(R.string.critical_issue_found);
        else
            return getString(R.string.textOffSet) + numberOfCriticalIssues + getString(R.string.critical_issues_found);
    }

    private boolean isModerateHazard(Inspection inspection) {
        return inspection.getHazardRating().equals("Moderate");
    }

    private boolean isLowHazard(Inspection inspection) {
        return inspection.getHazardRating().equals("Low");
    }

    //Return a String which tells how long ago a specific inspection happened
    public String inspectionTime(Inspection inspection){
        long different = inspection.inspectionTimeDifferent();
        String inspectionTimeInSring;

        if(different <= 30) inspectionTimeInSring = (getString(R.string.inspection) + different + getString(R.string.days_ago));
        else if(different <= 365) inspectionTimeInSring = (getString(R.string.inspection_on) + inspection.getInspectionMonth() + getString(R.string.space) + inspection.getInspectionDay() + getString(R.string.colon));
        else inspectionTimeInSring = (getString(R.string.inspection_on) + inspection.getInspectionMonth() + getString(R.string.space) + inspection.getInspectionYear() + getString(R.string.colon));

        return inspectionTimeInSring;
    }
}
