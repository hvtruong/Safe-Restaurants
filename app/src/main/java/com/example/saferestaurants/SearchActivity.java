package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.saferestaurants.model.Restaurants;

public class SearchActivity extends AppCompatActivity {

    private String[] hazardLevels = {"Low", "Moderate", "High"};
    private String[] inequalities = {">=", "<="};

    private Restaurants allRestaurants = Restaurants.getInstance();
    private Restaurants searchRestaurants = Restaurants.getSearchInstance();

    private String searchTerm;
    private String criticalIssueInequality;
    private int criticalIssueCount;
    private String hazardLevel;
    private boolean onlyFavourites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        

        populateSpinners();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void populateSpinners() {
        Spinner hazardSpinner = (Spinner) findViewById(R.id.spinnerHazardLevel);
        ArrayAdapter<String> hazardAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, hazardLevels);
        hazardSpinner.setAdapter(hazardAdapter);

        Spinner criticalSpinner = (Spinner) findViewById(R.id.spinnerCriticalViolationCount);
        ArrayAdapter<String> criticalAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, inequalities);
        criticalSpinner.setAdapter(criticalAdapter);
    }




}