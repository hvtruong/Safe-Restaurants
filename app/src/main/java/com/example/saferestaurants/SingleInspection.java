package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import java.util.Calendar;
import java.util.Date;

public class SingleInspection extends AppCompatActivity {
    Restaurants restaurants = Restaurants.getInstance();
    int restaurantID;
    int inspectionID;
    Inspection inspection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_inspection);

        Date currentDate = Calendar.getInstance().getTime();
        extractData();
        Toast.makeText(this,inspection.getDate().toString(),Toast.LENGTH_LONG).show();
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
}
