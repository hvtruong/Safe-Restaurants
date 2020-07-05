package com.example.saferestaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetail extends AppCompatActivity {
    Restaurants restaurants = Restaurants.getInstance();
    Restaurant restaurant;
    int restaurantID;
    ListView inspectionListView;
    private List<Inspection> inspectionList = new ArrayList<Inspection>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        extractRestaurant();
        inspectionList = (List<Inspection>) restaurant.getInspection();

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
        restaurantID = intent.getIntExtra("restaurantID",0);
        restaurant = restaurants.get(restaurantID);
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
    /*public void setUpListView(){
        inspectionListView = findViewById(R.id.inspectionListView);

        // ArrayList of String to store all the information displayed in the listView
        ArrayList<String> inspectionsList = new ArrayList<>();
        for(int i = 0; i < restaurant.getInspection().size(); i++){
            inspectionsList.add(inspectionTime(restaurant.getInspection().get(i)) + getString(R.string.empty) + restaurant.getInspection().get(i).getCriticalIssues() +getString(R.string.critical_issues_found) + restaurant.getInspection().get(i).getNonCriticalIssues() + getString(R.string.non_critical_issues_found));
        }

        //Create an ArrayAdapter, changing colors and icons for hazard level
        ArrayAdapter inspectionsAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,inspectionsList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                String inspectionHazardLevel = restaurant.getInspection().get(position).getHazardRating();
                View view = super.getView(position, convertView, parent);
                if(inspectionHazardLevel.equals("Low"))
                    view.setBackgroundColor(Color.GREEN);
                else if(inspectionHazardLevel.equals("Moderate"))
                    view.setBackgroundColor(Color.YELLOW);
                else
                    view.setBackgroundColor(Color.RED);

                return view;
            }
        };
        inspectionListView.setAdapter(inspectionsAdapter);

        inspectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent singleInspection = SingleInspection.makeIntent(RestaurantDetail.this, restaurantID, position);
                startActivity(singleInspection);
            }
        });
    }*/

    public void setUpListView(){
        inspectionListView = findViewById(R.id.inspectionListView);
        ArrayAdapter<Inspection> inspectionArrayAdapter = new inspectionAdapter();
        inspectionListView.setAdapter(inspectionArrayAdapter);
    }

    private class inspectionAdapter extends ArrayAdapter<Inspection>{
        public inspectionAdapter(){
            super(RestaurantDetail.this,R.layout.item_view, inspectionList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;

            if(itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.item_view,parent,false);

            Inspection currentInspection = inspectionList.get(position);
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

            TextView inspectionInfo = findViewById(R.id.inspectionInfo);
            inspectionInfo.setText(inspectionTime(currentInspection) + getString(R.string.empty) + currentInspection.getCriticalIssues() +getString(R.string.critical_issues_found) + currentInspection.getNonCriticalIssues() + getString(R.string.non_critical_issues_found));
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
