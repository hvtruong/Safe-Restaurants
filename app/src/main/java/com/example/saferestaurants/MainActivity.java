package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

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

        Restaurants restaurants = Restaurants.getInstance();
        Button btn = findViewById(R.id.button);
        //Toast.makeText(this, restaurants.size(),Toast.LENGTH_LONG).show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = RestaurantDetail.makeIntent(MainActivity.this,1);
                startActivity(intent);
            }
        });
    }
}
