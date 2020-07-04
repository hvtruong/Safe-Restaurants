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
import android.widget.ListView;
import android.widget.TextView;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import java.util.ArrayList;

public class RestaurantDetail extends AppCompatActivity {
    Restaurants restaurants = Restaurants.getInstance();
    Restaurant restaurant;
    int restaurantID;
    ListView inspectionListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        extractRestaurant();

        setUpTitle();
        setUpListView();
    }

    public static Intent makeIntent(Context context, int restaurantID){
        Intent intent = new Intent(context, RestaurantDetail.class);
        intent.putExtra("restaurantID",restaurantID);
        return intent;
    }

    public void setUpTitle(){
        TextView textView = findViewById(R.id.restaurantName);
        TextView textView1 = findViewById(R.id.restaurantAddress);
        TextView textView2 = findViewById(R.id.restaurantGPS);
        textView.setText(restaurant.getName());
        textView1.setText(getString(R.string.address) + restaurant.getPhysicalAddress());
        textView2.setText(getString(R.string.gps) + restaurant.getLatitude() + "\t \t" + restaurant.getLongitude());
    }

    public void setUpListView(){
        inspectionListView = findViewById(R.id.inspectionListView);

        // ArrayList of String to store all the information displayed in the listView
        ArrayList<String> inspectionsList = new ArrayList<>();
        for(int i = 0; i < restaurant.getInspection().size(); i++){
            inspectionsList.add(inspectionTime(restaurant.getInspection().get(i)) + getString(R.string.empty) + restaurant.getInspection().get(i).getCriticalIssues() +getString(R.string.critical_issues_found) + restaurant.getInspection().get(i).getNonCriticalIssues() + getString(R.string.non_critical_issues_found));
        }

        //Create an ArrayAdapter
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
    }

    public String inspectionTime(Inspection inspection){
        long different = inspection.inspectionTimeDifferent();
        String inspectionTimeInSring;

        if(different <= 30) inspectionTimeInSring = (getString(R.string.inspection) + different + getString(R.string.days_ago));
        else if(different <= 365) inspectionTimeInSring = (getString(R.string.inspection_on) + inspection.getInspectionMonth() + getString(R.string.space) + inspection.getInspectionDay() + getString(R.string.colon));
        else inspectionTimeInSring = (getString(R.string.inspection_on) + inspection.getInspectionMonth() + getString(R.string.space) + inspection.getInspectionYear() + getString(R.string.colon));

        return inspectionTimeInSring;
    }

    public void extractRestaurant(){
        Intent intent = getIntent();
        restaurantID = intent.getIntExtra("restaurantID",0);
        restaurant = restaurants.get(restaurantID);
    }
}
