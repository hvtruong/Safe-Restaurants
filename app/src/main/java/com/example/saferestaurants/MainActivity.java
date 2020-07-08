package com.example.saferestaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
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

        if(restaurants.size() == 0)
            setData();

        Toolbar toolbar = findViewById(R.id.mainActivityBar);
        toolbar.setTitle("Safe Restaurants");

        setUpListView();
    }

    private void setData() {

        // Setting up Restaurants Class Data //
        InputStream inputStreamRestaurants = getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader readerRestaurants = new BufferedReader(
                new InputStreamReader(inputStreamRestaurants, Charset.forName("UTF-8"))
        );
        DataParser.parseRestaurants(readerRestaurants);
        //                                  //

        // Setting up Inspections Data for each Restaurant //
        InputStream inputStreamInspections = getResources().openRawResource(R.raw.inspectionreports_itr1);
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
            //Make sure we have a view to work with
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

            //find the Restaurant
            Restaurant restaurant = restaurants.get(position);
            //Fill the view
            //Adding the name
            TextView name = (TextView) itemView.findViewById(R.id.RestaurantName);
            name.setText(restaurant.getName());
            //adding the # of issues
            TextView issues = (TextView) itemView.findViewById(R.id.issuesFound);
            if(restaurant.getInspection().size() == 0){
                issues.setText(R.string.zeroIssues);
            } else {
                int issuesFound = getNumberOfIssues(restaurant.getInspection());
                issues.setText(getString(R.string.numOfIssues) + " " + issuesFound);
            }
            //adding the date of last inspection
            TextView lastInspectionDate = (TextView) itemView.findViewById(R.id.date);
            lastInspectionDate.setText(getDateOfLastInspection(restaurant.getInspection()));
            //setting an image
            ImageView imageView = (ImageView) itemView.findViewById(R.id.restaurantImage);
            imageView.setImageResource(R.drawable.plate);
            //set hazard level image and colour;
            ImageView hazardImage = (ImageView) itemView.findViewById(R.id.harzardLevelImage);

            //check the hazard levels and add image
            //first if statement is to check if the list is empty or not.
            if(restaurant.getInspection().size() == 0) {
                hazardImage.setImageResource(R.drawable.low_hazard);
                return itemView;
            }

            Inspection inspection = restaurant.getInspection().get(0); //get the latest inspection

            if(inspection.getHazardRating().equals(getString(R.string.low))){
                hazardImage.setImageResource(R.drawable.low_hazard);
            } else if(inspection.getHazardRating().equals(getString(R.string.moderate))){
                hazardImage.setImageResource(R.drawable.moderate_hazard);
            } else{
                hazardImage.setImageResource(R.drawable.high_hazard);
            }


            return itemView;
        }
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
        //System.out.println("System year "+ year );
        //inspection date info
        //System.out.println(tmp.getInspectionMonth());
        int inspectionMonth = monthStringToInt(tmp.getInspectionMonth());
        int inspectionDay = Integer.parseInt(tmp.getInspectionDay());
        int inspectionYear = Integer.parseInt(tmp.getInspectionYear());
        //System.out.println("non system year: "  + inspectionYear);

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
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for(int i = 0; i < months.length; i++){
            if(month.equals(months[i])){
                return i;
            }
        }
        return -1;
    }
}
