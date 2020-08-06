//This class is to let users filter their searches
package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FilterOption extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Spinner spinner;
    private EditText specific_hazardLevel;
    private EditText numberOfCrit;
    private RadioButton radioButton;
    private Button clearButton;
    private int savedPosition;
    private static final String SHARED_PREF = "sharedPrefs";
    private static final String numOfCrit = "searchedNumber";
    private static final String inequality = "inequality";
    private static final String specificHazardLevel = "hazardLevel";
    private static final String onlyFavorite = "favorite";
    private static final String position = "savedPosition";
    private boolean onlyFavoriteDisplayed = false;
    private String chosenInequality = "";
    public static boolean filterSaved = false;
    private int numberOfCritical = -1;
    private String searchedHazardLevel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_option);
        Toolbar toolbar = findViewById(R.id.FilterOptionToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        specific_hazardLevel = findViewById(R.id.specificSearchHazard);
        numberOfCrit = findViewById(R.id.NumberOfSearchCriticalIssues);

        //Setup spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.signs,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
        //End of setting up spinner

        radioButton = findViewById(R.id.radioButton);

        View.OnTouchListener radioButtonListener = new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(((RadioButton) v).isChecked()){
                    radioButton.setChecked(false);
                    onlyFavoriteDisplayed = false;
                    return true;
                }
                return false;
            }
        };
        radioButton.setOnTouchListener(radioButtonListener);

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlyFavoriteDisplayed = true;
            }
        });

        Button save = findViewById(R.id.saveSearch);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("useSearchResults", false);
                editor.apply();
                clear();
            }
        });

        //Load and display current search filter
        loadData();
        displayCurrentSearchFilter();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    //Save data to apply to Map
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        searchedHazardLevel = specific_hazardLevel.getText().toString().toLowerCase();
        String numberOfSearchedCrit = numberOfCrit.getText().toString();
        if(!numberOfSearchedCrit.equals("")){
            numberOfCritical = Integer.parseInt(numberOfSearchedCrit);
        }
        else{
            numberOfCritical = -1;
        }

        if(!searchedHazardLevel.equals("") && !searchedHazardLevel.equals("low") &&
        !searchedHazardLevel.equals("moderate") && !searchedHazardLevel.equals("high")){
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
            return;
        }

        editor.putString(inequality, chosenInequality);
        editor.putInt(numOfCrit,numberOfCritical);
        editor.putString(specificHazardLevel, searchedHazardLevel);
        editor.putBoolean(onlyFavorite, onlyFavoriteDisplayed);
        editor.putInt(position,savedPosition);

        editor.putBoolean("useSearchResults", true);

        editor.apply();

        filterSaved = true;
        Intent backIntent = new Intent(FilterOption.this,MapsActivity.class);
        setResult(Activity.RESULT_CANCELED,backIntent);
        finish();
    }

    //Load and saved filters
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        chosenInequality = sharedPreferences.getString(inequality, "");
        numberOfCritical = sharedPreferences.getInt(numOfCrit,-1);
        searchedHazardLevel = sharedPreferences.getString(specificHazardLevel,"");
        onlyFavoriteDisplayed = sharedPreferences.getBoolean(onlyFavorite, false);
        savedPosition = sharedPreferences.getInt(position,0);
    }

    //Display saved filters
    private void displayCurrentSearchFilter(){
        String numberOfSavedCritical = getString(R.string.empty);
        spinner.setSelection(savedPosition);
        specific_hazardLevel.setText(searchedHazardLevel);
        if(numberOfCritical != -1){
            numberOfSavedCritical = numberOfSavedCritical + numberOfCritical;
        }
        numberOfCrit.setText(numberOfSavedCritical);
        radioButton.setChecked(onlyFavoriteDisplayed);
    }

    //Clear all the filters
    private void clear(){
        chosenInequality = spinner.getItemAtPosition(0).toString();
        numberOfCrit.setText(getString(R.string.empty));
        specific_hazardLevel.setText(getString(R.string.empty));
        savedPosition = 0;
        spinner.setSelection(savedPosition);
        onlyFavoriteDisplayed = false;
        radioButton.setChecked(onlyFavoriteDisplayed);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenInequality = parent.getItemAtPosition(position).toString();
        savedPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}