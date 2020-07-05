package com.example.saferestaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    private Restaurants restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setData();
        restaurants = Restaurants.getInstance();

        // these lines can be removed once the mainactivity has a clickable list view //
        //Intent i = RestaurantDetail.makeIntent(MainActivity.this, 0);
        //startActivity(i);
        //                                                                            //

        setUpListView();      // this can be uncommented when list view works //
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
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Make sure we have a view to work with
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurants_item_view, parent, false);
            }

            //find the Restaurant
            Restaurant restaurant = restaurants.get(position);
            //Fill the view
            //Adding the name
            TextView name = (TextView) itemView.findViewById(R.id.RestaurantName);
            name.setText(restaurant.getName());
            //adding the # of issues
            TextView issues = (TextView) itemView.findViewById(R.id.issuesFound);
            int issuesFound = getNumberOfIssues(restaurant.getInspection());
            issues.setText("Number Of issues: " + issuesFound);
            //adding the date of last inspection
            TextView lastInspectionDate = (TextView) itemView.findViewById(R.id.date);
            lastInspectionDate.setText("Last Inspection: " + getDateOfLastInspection(restaurant.getInspection()));

            return itemView;
        }
    }

    private String getDateOfLastInspection(Inspections inspection) {
        if(inspection.size() == 0){
            return "No Inspections Done";
        }
        Inspection tmp = inspection.get(0);
        Date date = tmp.getDate();
        //getting current date
        int month = Calendar.getInstance().get(Calendar.MONTH);
        System.out.println("System month "+ month );
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        //inspection date info
        System.out.println(tmp.getInspectionMonth());
        int inspectionMonth = monthStringToInt(tmp.getInspectionMonth());
        System.out.println("non system month: "  + inspectionMonth);
        int inspectionDay = Integer.parseInt(tmp.getInspectionDay());
        int inspectionYear = Integer.parseInt(tmp.getInspectionYear());

        //Date formating depending when inspection was done
        if(month == inspectionMonth && year == inspectionYear){
            String lessThanMonthFormat = "Last Inspection: " + (day - inspectionDay) + " days ago";
            return lessThanMonthFormat;
        }
        //dw ill change it
        return "something went wrong :(";
    }

    /**
     * returns the total Number of critical and noncritical issues
     *
     * @param inspection
     * @return total issues
     */
    private int getNumberOfIssues(Inspections inspection) {
        int issues = 0;

        for (int i = 0; i < inspection.size(); i++) {
            Inspection tmp = inspection.get(0);
            issues += tmp.getCriticalIssues();
            issues += tmp.getNonCriticalIssues();
        }
        return issues;
    }

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
