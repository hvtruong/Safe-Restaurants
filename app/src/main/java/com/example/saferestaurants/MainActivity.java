/**
 * File Name: MainActivity.java
 * Date completed: July 8, 2020
 * Completed by: Jaiveer Dhanju
 * Purpose: This is the main activity, which is the start of the app.
 *          At the beginning this activity displays restaurants and
 *          information about there most recent health inspection
 */
package com.example.saferestaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Inspections;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    //Fields
    private Restaurants restaurants = Restaurants.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataFetcher.setFileLocation(getFilesDir().toString());
        new DataFetcher.RetrieveData().execute();

        if(isRestaurantsEmpty())
            setData();

        Toolbar toolbar = findViewById(R.id.mainActivityBar);
        toolbar.setTitle(R.string.safe_restaurants);

        setUpListView();

    }

    private boolean isRestaurantsEmpty() {
        return restaurants.size() == 0;
    }

    private void setData() {

        // Setting up Restaurants Class Data //
        FileInputStream inputStreamRestaurants = null;
        try {
            File file = new File(getFilesDir().toString() + "/" + "restaurants_itr2.csv");
            for (String filee : getFilesDir().list()) {
                System.out.println(filee);
            }

            inputStreamRestaurants = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // InputStream inputStreamRestaurants = getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader readerRestaurants = new BufferedReader(
                new InputStreamReader(inputStreamRestaurants, Charset.forName("UTF-8"))
        );
        DataParser.parseRestaurants(readerRestaurants);
        //                                  //

        // Setting up Inspections Data for each Restaurant //
        InputStream inputStreamInspections = null;
        try {
            File file = new File(getFilesDir().toString() + "/" + "inspectionreports_itr2.csv");
            for (String filee : getFilesDir().list()) {
                System.out.println(filee);
            }

            inputStreamInspections = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //InputStream inputStreamInspections = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader readerInspections = new BufferedReader(
                new InputStreamReader(inputStreamInspections, Charset.forName("UTF-8"))
        );
        DataParser.parseInspections(readerInspections);
        //                                                //
    }

    private void setUpListView() {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.restaurantsListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> {

        public MyListAdapter() {
            super(MainActivity.this, R.layout.restaurants_item_view, restaurants.getList());

        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurants_item_view, parent, false);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = RestaurantDetail.makeIntent(MainActivity.this, position);
                    startActivity(i);
                }
            });

            Restaurant restaurant = restaurants.get(position);

            TextView name = (TextView) itemView.findViewById(R.id.RestaurantName);
            name.setText(restaurant.getName());

            TextView issues = (TextView) itemView.findViewById(R.id.issuesFound);
            issues.setText(getStringOfIssues(restaurant));

            TextView lastInspectionDate = (TextView) itemView.findViewById(R.id.date);
            lastInspectionDate.setText(getDateOfLastInspection(restaurant.getInspection()));

            ImageView imageView = (ImageView) itemView.findViewById(R.id.restaurantImage);
            imageView.setImageResource(R.drawable.plate);

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

    private boolean isModerateHazard(Inspection inspection) {
        return inspection.getHazardRating().equals(getString(R.string.moderate));
    }

    private boolean isLowHazard(Inspection inspection) {
        return inspection.getHazardRating().equals(getString(R.string.low));
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

    /**
     * returns the total Number of critical and noncritical issues
     *
     * @param inspection
     * @return total issues
     */
    private int getNumberOfIssues(Inspections inspection) {
        int issues = 0;

        Inspection tmp = inspection.get(0);
        issues += tmp.getCriticalIssues();
        issues += tmp.getNonCriticalIssues();

        return issues;
    }

    /**
     * converting months that are in strings to int's
     * @param month
     * @return
     */
    private int monthStringToInt(String month){
        String[] months = {getString(R.string.jan), getString(R.string.feb), getString(R.string.mar), getString(R.string.apr), getString(R.string.may), getString(R.string.jun), getString(R.string.jul), getString(R.string.aug), getString(R.string.sep), getString(R.string.oct), getString(R.string.nov), getString(R.string.dec)};
        for(int i = 0; i < months.length; i++){
            if(month.equals(months[i])){
                return i;
            }
        }
        return -1;
    }
}