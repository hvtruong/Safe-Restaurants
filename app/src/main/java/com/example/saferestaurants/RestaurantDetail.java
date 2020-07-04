package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RestaurantDetail extends AppCompatActivity {
    Restaurant restaurant;
    int restaurantID;
    ArrayAdapter inspectionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        try {
            DataParser.parseRestaurants("src/main/java/com/example/saferestaurants/ProjectData/restaurants_itr1.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DataParser.parseInspections("src/main/java/com/example/saferestaurants/ProjectData/inspectionreports_itr1.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Restaurants restaurants = Restaurants.getInstance();
        restaurant = restaurants.get(2);

        setUpTitle();
        setUpListView();
    }

    public static Intent makeIntent(Context context, int restaurantID){
        Intent intent = new Intent(context, RestaurantDetail.class);
        intent.putExtra("restaurantID",restaurantID);
        return intent;
    }

    public void setUpTitle(){
        TextView textView = findViewById(R.id.textview);
        TextView textView1 = findViewById(R.id.textview2);
        TextView textView2 = findViewById(R.id.textview3);
        textView.setText(restaurant.getName());
        textView1.setText(restaurant.getPhysicalAddress());
        textView2.setText(restaurant.getLatitude() + "\t" + restaurant.getLongitude());
    }

    public void setUpListView(){
        ListView inspectionListView = findViewById(R.id.inspectionListView);

        // ArrayList of String to store all the information displayed in the listView
        ArrayList<String> inspectionsList = new ArrayList<>();
        for(int i = 0; i < restaurant.getInspection().size(); i++){
            inspectionsList.add( getString(R.string.empty) + restaurant.getInspection().get(i).getCriticalIssues() +getString(R.string.critical_issues_found) + restaurant.getInspection().get(i).getNonCriticalIssues() + getString(R.string.non_critical_issues_found));
        }

        inspectionsAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,inspectionsList);
        inspectionListView.setAdapter(inspectionsAdapter);
        setUpHazardColor();

        inspectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent singleInspection = SingleInspection.makeIntent(RestaurantDetail.this, restaurantID, position);
                startActivity(singleInspection);
            }
        });
    }

    public void setUpHazardColor(){
        for(int i = 0; i < restaurant.getInspection().size(); i++){
            return;
        }
    }

    /*public void extractRestaurant(){
        Intent intent = getIntent();
        int restaurantID = intent.getIntExtra("restaurantID",0);
        restaurant = restaurants.get(restaurantID);
    }*/
}
