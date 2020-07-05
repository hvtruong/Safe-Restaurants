package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import java.util.Calendar;
import java.util.Date;

public class SingleInspection extends AppCompatActivity {
    Restaurants restaurants = Restaurants.getInstance();
    public static final String SHARED_PREF = "sharedPrefs";
    public static final String reservedRestaurantID = "restaurantID";
    int restaurantID;
    int inspectionID;
    Inspection inspection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_inspection);
        Toolbar toolbar = findViewById(R.id.singleInspectionToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        extractData();
        saveRestaurantID();
    }

    public static Intent makeIntent(Context context, int restaurantID, int inspectionID){
        Intent intent = new Intent(context, SingleInspection.class);
        intent.putExtra("restaurantID",restaurantID);
        intent.putExtra("inspectionID",inspectionID);
        return intent;
    }

    public void extractData(){
        Intent intent = getIntent();
        this.restaurantID = intent.getIntExtra("restaurantID",-1);
        this.inspectionID = intent.getIntExtra("inspectionID",-1);
        this.inspection = restaurants.get(restaurantID).getInspection().get(inspectionID);
    }

    public void saveRestaurantID(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(reservedRestaurantID, restaurantID);

        editor.apply();
    }
}
