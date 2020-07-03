package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import org.w3c.dom.Text;

public class RestaurantDetail extends AppCompatActivity {
    Restaurants restaurants = Restaurants.getInstance();
    Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        extractRestaurant();
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

    public void extractRestaurant(){
        Intent intent = getIntent();
        int restaurantID = intent.getIntExtra("restaurantID",0);
        restaurant = restaurants.get(restaurantID);
    }
}
