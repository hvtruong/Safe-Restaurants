package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import java.util.ArrayList;

import static com.example.saferestaurants.SingleInspection.SHARED_PREF;

public class SearchActivity extends AppCompatActivity {

    private final String SHARED_PREF = "sharedPrefs";
    private final String numOfCrit = "searchedNumber";
    private final String inequality = "inequality";
    private final String specificHazardLevel = "hazardLevel";
    private final String onlyFavorite = "favorite";
    private final String searchTermName = "searchTerm";

    private String[] hazardLevels = {"", "low", "moderate", "high"};
    private String[] inequalities = {"(None)", ">=", "<="};

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

        
        setUpButtons();
        populateSpinners();
        populateCriteria();

    }

    private void retrieveDataFromFields() {
        Spinner spinnerInequality = (Spinner) findViewById(R.id.spinnerCriticalViolationCount);
        criticalIssueInequality = spinnerInequality.getSelectedItem().toString();

        TextView entryCriticalIssues = (TextView) findViewById(R.id.entryCriticalViolationCount);
        if (entryCriticalIssues.getText().toString().equals("") || spinnerInequality.getSelectedItem().toString().equals(inequalities[0])) {
            criticalIssueCount = -1;

        } else {
            criticalIssueCount = Integer.parseInt(entryCriticalIssues.getText().toString());
        }


        Spinner spinnerHazardLevel = (Spinner) findViewById(R.id.spinnerHazardLevel);
        hazardLevel = spinnerHazardLevel.getSelectedItem().toString();

        CheckBox checkFavourite = (CheckBox) findViewById(R.id.checkFavourite);
        onlyFavourites = checkFavourite.isChecked();

        TextView entrySearchTerm = (TextView) findViewById(R.id.entrySearchField);
        searchTerm = entrySearchTerm.getText().toString();
    }

    private void populateCriteria() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        // change the fields to reflect the sharedprefs

        criticalIssueInequality = sharedPreferences.getString(inequality, "");
        Spinner spinnerInequality = (Spinner) findViewById(R.id.spinnerCriticalViolationCount);
        spinnerInequality.setSelection(java.util.Arrays.asList(inequalities).indexOf(criticalIssueInequality));

        criticalIssueCount = sharedPreferences.getInt(numOfCrit,-1);
        if (criticalIssueCount != -1) {
            TextView entryCriticalIssues = (TextView) findViewById(R.id.entryCriticalViolationCount);
            entryCriticalIssues.setText(Integer.toString(criticalIssueCount));
        }

        hazardLevel = sharedPreferences.getString(specificHazardLevel,"").toLowerCase();
        Spinner spinnerHazardLevel = (Spinner) findViewById(R.id.spinnerHazardLevel);
        spinnerHazardLevel.setSelection(java.util.Arrays.asList(hazardLevels).indexOf(hazardLevel));

        onlyFavourites = sharedPreferences.getBoolean(onlyFavorite, false);
        CheckBox checkFavourite = (CheckBox) findViewById(R.id.checkFavourite);
        checkFavourite.setChecked(onlyFavourites);

        searchTerm = sharedPreferences.getString("searchTerm", "");
        TextView entrySearchTerm = (TextView) findViewById(R.id.entrySearchField);
        entrySearchTerm.setText(searchTerm);
    }

    private void startMainActivity() {
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        startMainActivity();
    }

    public static void updateSearchList(Restaurants allRestaurants, String hazardLevel,
                                         int criticalIssueCount, String criticalIssueInequality,
                                         Restaurants searchRestaurants, boolean onlyFavourites,
                                         String searchTerm) {

        ArrayList<Restaurant> fullList = allRestaurants.getList();
        ArrayList<Restaurant> newList = new ArrayList<>();

        for (Restaurant restaurant : fullList) {
            // Hazard level
            /*
            if (this.hazardLevel.equals("") || restaurant.getInspection().get(0).getHazardRating().equals(this.hazardLevel)) {
                if (criticalIssueCount == -1 || (criticalIssueInequality.equals("<=") &&
                        restaurant.getInspection().get(0).getCriticalIssues() <= criticalIssueCount) || (!this.hazardLevel.equals("") &&
                        restaurant.getInspection().get(0).getHazardRating().equals(this.hazardLevel))) {
                    if (this.onlyFavourites && restaurant.isFavorite() || !this.onlyFavourites) {
                        if (restaurant.getName().contains(this.searchTerm)) {
                            if (this.hazardLevel.equals("") || restaurant.getInspection().size() != 0) {
                                if (restaurant.getInspection().get(0).getHazardRating().equals(this.hazardLevel))
                            }
                            newList.add(restaurant);
                        }
                    }
                }
            }

             */

            if (restaurant.getInspection().size() == 0) {
                if (!hazardLevel.equals("")) { continue; }
            } else if (hazardLevel.equals("") || restaurant.getInspection().get(0).getHazardRating().toLowerCase().equals(hazardLevel)) {
                ;
            } else {
                continue;
            }

            // Critical issue inequality
            if (criticalIssueCount != -1) {

                if (restaurant.getInspection().size() == 0) { continue; }

                if (criticalIssueInequality.equals("<=")) {
                    if (restaurant.getInspection().get(0).getCriticalIssues() <= criticalIssueCount) {
                        ;
                    } else { continue; }
                }

                if (criticalIssueInequality.equals(">=")) {
                    if (restaurant.getInspection().get(0).getCriticalIssues() >= criticalIssueCount) {
                        ;
                    } else { continue; }
                }
            }

            // Favourite
            if (!onlyFavourites || restaurant.isFavorite()) {
                ;
            } else { continue; }

            // Check if the search term is in the restaurant name.
            if (restaurant.getName().contains(searchTerm)) {
                ;
            } else { continue; }

            // Add the restaurant to the new list if all search criteria are met.
            newList.add(restaurant);
        }

        // Set the search instance of the restaurant manager to the newly created list.
        searchRestaurants.setList(newList);



    }

    private void setUpButtons() {
        Button clear = (Button) findViewById(R.id.buttonClear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("useSearchResults", false);
                editor.apply();

                startMainActivity();
            }
        });

        Button save = (Button) findViewById(R.id.buttonSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveDataFromFields();

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("useSearchResults", true);
                editor.putString(searchTermName, searchTerm);
                editor.putString(inequality, criticalIssueInequality);
                editor.putInt(numOfCrit, criticalIssueCount);
                editor.putString(specificHazardLevel, hazardLevel);
                editor.putBoolean(onlyFavorite, onlyFavourites);
                editor.apply();



                updateSearchList(allRestaurants, hazardLevel, criticalIssueCount,
                        criticalIssueInequality, searchRestaurants, onlyFavourites, searchTerm);



                // Go back to main activity.
                startMainActivity();
            }
        });
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