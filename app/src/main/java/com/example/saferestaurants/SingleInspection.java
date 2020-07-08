package com.example.saferestaurants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Restaurants;
import com.example.saferestaurants.model.Violation;

import java.util.ArrayList;

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


        // Organize data into arrays for iterative updating of TextViews.
        int[] textViews = {R.id.textHazardLevel, R.id.textInspectionDate, R.id.textInspectionType, R.id.textCritical, R.id.textNonCritical};
        String[] textPrefixes = {"Hazard Level: ", "Date: ", "Type: ", "Critical Issues: ", "Non-Critical Issues: "};

        String inspectionDate = (inspection.getInspectionMonth() + " " +
                inspection.getInspectionDay() + ", " +
                inspection.getInspectionYear()
        );

        String[] inspectionDetails = {
                inspection.getHazardRating(),
                inspectionDate,
                inspection.getType(),
                Integer.toString(inspection.getCriticalIssues()),
                Integer.toString(inspection.getNonCriticalIssues())
        };

        // Display information into the relevant TextViews.
        for (int i = 0; i < textViews.length; i++) {
            TextView view = findViewById(textViews[i]);
            view.setText(textPrefixes[i] + inspectionDetails[i]);
        }

        ImageView ratingImage = findViewById(R.id.imageHazardLevel);
        TextView ratingText = findViewById(R.id.textHazardLevel);
        switch (inspection.getHazardRating()) {
            case "Low":
                ratingText.setBackgroundColor(Color.parseColor("#9090FF81"));
                ratingImage.setImageResource(R.drawable.low_hazard);
                break;
            case "Moderate":
                ratingText.setBackgroundColor(Color.parseColor("#88FFD372"));
                ratingImage.setImageResource(R.drawable.moderate_hazard);
                break;
            case "High":
                ratingText.setBackgroundColor(Color.parseColor("#83FF8273"));
                ratingImage.setImageResource(R.drawable.high_hazard);
                break;
        }

        // Load violations into the ListView
        loadViolations();
    }

    private class ViolationsAdapter extends ArrayAdapter<Violation> {
        public ViolationsAdapter(Context context, ArrayList<Violation> violations) {
            super(context, android.R.layout.simple_list_item_1, violations);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Violation violation = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_violation, parent, false);
            }

            String description = violation.getDescription();
            String shortDescription = "Violation: " + description.substring(description.indexOf('['), description.indexOf(']') + 1);

            LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.itemLayout);
            TextView textDescription = (TextView) convertView.findViewById(R.id.violationDescription);
            TextView textCriticalRating = (TextView) convertView.findViewById(R.id.violationCriticalRating);
            TextView textCategory = (TextView) convertView.findViewById(R.id.violationCategoryText);
            ImageView imageCriticalRating;
            ImageView imageViolationCategory = (ImageView) convertView.findViewById(R.id.violationCategoryIcon);

            textCategory.setText("Category: " + violation.getType());

            switch(violation.getType()) {
                case "Food":
                    imageViolationCategory.setImageResource(R.drawable.food);
                    break;
                case "Pest":
                    imageViolationCategory.setImageResource(R.drawable.pest);
                    break;
                case "Equipment":
                    imageViolationCategory.setImageResource(R.drawable.equipment);
                    break;
                case "Sanitary":
                    imageViolationCategory.setImageResource(R.drawable.sanitary);
                    break;
                case "Employee":
                    imageViolationCategory.setImageResource(R.drawable.employee);
                    break;
                case "Building":
                    imageViolationCategory.setImageResource(R.drawable.building);
                    break;
                case "Qualifications":
                    imageViolationCategory.setImageResource(R.drawable.qualifications);
                    break;
            }

            imageCriticalRating = (ImageView) convertView.findViewById(R.id.violationCriticalIcon);
            if (violation.getCriticalValue().equals("Critical")) {
                imageCriticalRating.setImageResource(R.drawable.super_hazard_icon);
                itemLayout.setBackgroundColor(Color.parseColor("#83FF8273"));
            } else {
                imageCriticalRating.setImageResource(0);
                itemLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }


            textDescription.setText(shortDescription);
            textCriticalRating.setText("Critical Rating: " + violation.getCriticalValue());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String toastMessage = violation.getDescription();
                    Toast.makeText(SingleInspection.this, toastMessage, Toast.LENGTH_LONG).show();
                }
            });

            return convertView;

        }

    }

    private void loadViolations() {
        ArrayAdapter<Violation> violationsAdapter = new ViolationsAdapter(this, inspection.getViolations());
        ListView violationsListView = findViewById(R.id.listViolations);
        violationsListView.setAdapter(violationsAdapter);
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
