package com.example.saferestaurants;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Inspections;
import com.example.saferestaurants.model.Restaurant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by: Jaiveer Dhanju
 * Purpose: Display all the users favorite Restaurants that have had a recent inspection
 * Date completed: August 5th 2020
 */
public class FavActivity extends AppCompatActivity {
    private ArrayList<Restaurant> favList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        favList = MapsActivity.updatedFavList;

        setUpListView();
        Toolbar toolbar = findViewById(R.id.FavActivityBar);
        toolbar.setTitle(R.string.updateFav);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpListView() {
        ArrayAdapter<Restaurant> adapter = new FavActivity.MyListAdapter();
        ListView list = (ListView) findViewById(R.id.FavListView);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> {

        public MyListAdapter() {
            super(FavActivity.this, R.layout.restaurants_item_view, favList);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurants_item_view, parent, false);
            }

            Restaurant restaurant = favList.get(position);
            TextView name = (TextView) itemView.findViewById(R.id.RestaurantName);
            name.setText(restaurant.getName());

            TextView issues = (TextView) itemView.findViewById(R.id.issuesFound);
            issues.setText(getStringOfIssues(restaurant));

            TextView lastInspectionDate = (TextView) itemView.findViewById(R.id.date);
            lastInspectionDate.setText(getDateOfLastInspection(restaurant.getInspection()));

            ImageView imageView = (ImageView) itemView.findViewById(R.id.restaurantImage);

            imageView.setImageResource(R.drawable.plate);
            setRestaurantIcon(imageView, restaurant.getName());

            //set hazard level image and colour;
            ImageView hazardImage = (ImageView) itemView.findViewById(R.id.harzardLevelImage);

            //first if statement is to check if the list is empty or not.
            if(!isInspectionsEmpty(restaurant)){
                //get the latest inspection
                Inspection inspection = restaurant.getInspection().get(0);

                //check the hazard levels and add image
                if(isLowHazard(inspection)){
                    hazardImage.setImageResource(R.drawable.low_hazard);
                } else if(isModerateHazard(inspection)){
                    hazardImage.setImageResource(R.drawable.moderate_hazard);
                } else{
                    hazardImage.setImageResource(R.drawable.high_hazard);
                }
            }

            return itemView;
        }
    }

    private String getStringOfIssues(Restaurant restaurant){
        if(isInspectionsEmpty(restaurant)){
            return getString(R.string.zeroIssues);
        }
        int issuesFound = getNumberOfIssues(restaurant.getInspection());
        return getString(R.string.numOfIssues) + " " + issuesFound;
    }

    private int getNumberOfIssues(Inspections inspection) {
        int issues = 0;

        Inspection tmp = inspection.get(0);
        issues += tmp.getCriticalIssues();
        issues += tmp.getNonCriticalIssues();

        return issues;
    }

    private boolean isInspectionsEmpty(Restaurant restaurant){
        return restaurant.getInspection().size() == 0;
    }

    private String getDateOfLastInspection(Inspections inspection) {
        if(inspection.size() == 0){
            return getString(R.string.noInspection);
        }
        Inspection tmp = inspection.get(0);
        Date date = tmp.getDate();
        //getting current date
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);

        int inspectionMonth = monthStringToInt(tmp.getInspectionMonth());
        int inspectionDay = Integer.parseInt(tmp.getInspectionDay());
        int inspectionYear = Integer.parseInt(tmp.getInspectionYear());

        //Date formatting depending when inspection was done
        if(month == inspectionMonth && year == inspectionYear){
            String lessThanMonthFormat = getString(R.string.lastInspection) + " "+  (day - inspectionDay) + getString(R.string.daysAgo);
            return lessThanMonthFormat;
        } else if(year == inspectionYear){
            String withinYearFormat = getString(R.string.lastInspection) + " " +  tmp.getInspectionMonth() + " " + inspectionDay;
            return withinYearFormat;
        } else{
            String pastYearFormat = getString(R.string.lastInspection) + " " + tmp.getInspectionMonth() + " " + inspectionDay + " " + inspectionYear;
            return pastYearFormat;
        }
    }

    private int monthStringToInt(String month){
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        for(int i = 0; i < months.length; i++){
            if(month.equals(months[i])){
                return i;
            }
        }
        return -1;
    }

    private void setRestaurantIcon(ImageView imageView, String restaurantName) {

        restaurantName = restaurantName.toLowerCase();

        if (restaurantName.contains("7-eleven")) {
            imageView.setImageResource(R.drawable.seven11);
        } else if (restaurantName.contains("a&w")) {
            imageView.setImageResource(R.drawable.aw);
        } else if (restaurantName.contains("boston pizza")) {
            imageView.setImageResource(R.drawable.bp);
        } else if (restaurantName.contains("domino's pizza")) {
            imageView.setImageResource(R.drawable.dominos);
        } else if (restaurantName.contains("mac's convenience store")) {
            imageView.setImageResource(R.drawable.macs);
        } else if (restaurantName.contains("mcdonald's")) {
            imageView.setImageResource(R.drawable.mcd);
        } else if (restaurantName.contains("pizza hut")) {
            imageView.setImageResource(R.drawable.pizzahut);
        } else if (restaurantName.contains("starbucks coffee")) {
            imageView.setImageResource(R.drawable.starbucks);
        } else if (restaurantName.contains("subway")) {
            imageView.setImageResource(R.drawable.subway);
        } else if (restaurantName.contains("tim hortons")) {
            imageView.setImageResource(R.drawable.timmies);
        }

    }

    private boolean isModerateHazard(Inspection inspection) {
        return inspection.getHazardRating().equals("Moderate");
    }

    private boolean isLowHazard(Inspection inspection) {
        return inspection.getHazardRating().equals("Low");
    }
}