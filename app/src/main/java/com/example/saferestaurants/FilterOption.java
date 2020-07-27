package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
    private static final String SHARED_PREF = "sharedPrefs";
    private static final String numOfCrit = "searchedNumber";
    private static final String inequality = "inequality";
    private static final String specificHazardLevel = "hazardLevel";
    private static final String onlyFavorite = "favorite";
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

        //Setup spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.signs,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
        //End of setting up spinner

        RadioButton radioButton = findViewById(R.id.radioButton);
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
                filterSaved = true;
                finish();
            }
        });
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        EditText specific_hazardLevel = findViewById(R.id.specificSearchHazard);
        searchedHazardLevel = specific_hazardLevel.getText().toString();
        EditText numberOfCrit = findViewById(R.id.NumberOfSearchCriticalIssues);
        String numberOfSearchedCrit = numberOfCrit.getText().toString();
        if(!numberOfSearchedCrit.equals("")){
            numberOfCritical = Integer.parseInt(numberOfSearchedCrit);
        }

        editor.putString(inequality, chosenInequality);
        editor.putInt(numOfCrit,numberOfCritical);
        editor.putString(specificHazardLevel, searchedHazardLevel);
        editor.putBoolean(onlyFavorite, onlyFavoriteDisplayed);

        editor.apply();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenInequality = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}