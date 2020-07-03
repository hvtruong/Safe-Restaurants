package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;

public class RestaurantDetail extends AppCompatActivity {
    Restaurant restaurant;

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
        TextView textView = findViewById(R.id.textview);
        TextView textView1 = findViewById(R.id.textview2);
        TextView textView2 = findViewById(R.id.textview3);
        textView.setText(restaurant.getName());
        textView1.setText(restaurant.getPhysicalAddress());
        textView2.setText(restaurant.getLatitude() + "\t" + restaurant.getLongitude());
    }

    public static Intent makeIntent(Context context, int restaurantID){
        Intent intent = new Intent(context, RestaurantDetail.class);
        intent.putExtra("restaurantID",restaurantID);
        return intent;
    }

    /*public void extractRestaurant(){
        Intent intent = getIntent();
        int restaurantID = intent.getIntExtra("restaurantID",0);
        restaurant = restaurants.get(restaurantID);
    }*/
}
