package com.example.saferestaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

public class RestaurantDetail extends AppCompatActivity {
    Restaurants restaurants = Restaurants.getInstance();
    Restaurant restaurant;
    int restaurantID;
    ListView inspectionListView;
    public static final String SHARED_PREF = "sharedPrefs";
    public static final String reservedRestaurantID = "restaurantID";

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
            String hazardLevel = currentInspection.getHazardRating();
            ImageView hazardIcon = itemView.findViewById(R.id.item_icon);

            if(hazardLevel.equals("Low")) {
                hazardIcon.setImageResource(R.drawable.healthy_food_icon);
                itemView.setBackgroundColor(Color.GREEN);
            }
            else if(hazardLevel.equals("Moderate")) {
                hazardIcon.setImageResource(R.drawable.warning_icon);
                itemView.setBackgroundColor(Color.YELLOW);
            }
            else{
                hazardIcon.setImageResource(R.drawable.super_hazard_icon);
                itemView.setBackgroundColor(Color.RED);
            }

            //Displaying summary of inspection information
            TextView inspectionDate = itemView.findViewById(R.id.inspectionDate);
            inspectionDate.setText(inspectionTime(currentInspection));

            TextView inspectionCriticalIssues = itemView.findViewById(R.id.inspectionNumberOfCriticalIssues);
            int numberOfCriticalIssues = currentInspection.getCriticalIssues();
            if(numberOfCriticalIssues == 0)
                inspectionCriticalIssues.setText(R.string.no_critical_issue_found);
            else if(numberOfCriticalIssues == 1)
                inspectionCriticalIssues.setText(getString(R.string.Line) + numberOfCriticalIssues + getString(R.string.critical_issues_found));
            else
                inspectionCriticalIssues.setText(getString(R.string.Line) + numberOfCriticalIssues + getString(R.string.critical_issues_found));

            TextView inspectionNonCriticalIssues = itemView.findViewById(R.id.inspectionNumberOfNonCriticalIssues);
            int numberOfNonCriticalIssues = currentInspection.getNonCriticalIssues();
            if(numberOfNonCriticalIssues == 0)
                inspectionNonCriticalIssues.setText(R.string.no_non_critical_issue_found);
            else if(numberOfNonCriticalIssues == 1)
                inspectionNonCriticalIssues.setText(getString(R.string.Line) + numberOfNonCriticalIssues + getString(R.string.non_critical_issues_found));
            else
                inspectionNonCriticalIssues.setText(getString(R.string.Line) + numberOfNonCriticalIssues + getString(R.string.non_critical_issues_found));

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
