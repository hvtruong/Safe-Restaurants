package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    //Fields
    Restaurants restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Parsing Data Files //
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
        //                   //

        restaurants = Restaurants.getInstance();
        setUpListView();

    }

    private void setUpListView() {
    }
}
