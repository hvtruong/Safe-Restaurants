// This class shows the restaurant list view activity and contains all the logic associated with that page //
package com.example.saferestaurants;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.example.saferestaurants.model.Restaurants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
public class MainActivity extends AppCompatActivity {
    //Fields
    public static Restaurants restaurants = Restaurants.getInstance();
    private Restaurants searchRestaurants = Restaurants.getSearchInstance();
    private static final String SHARED_PREF = "sharedPrefs";
    private ArrayList<Restaurant> favList;
    private String searchContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFavList();
        setUpListView();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.mainActivityBar);
        setSupportActionBar(myToolbar);

        loadSearchContent();

        if(getIntent().getStringExtra("searchContent") != null){
            extractSearchContent();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getFavList() {
        SharedPreferences prefs = getSharedPreferences("favList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("favList", null);
        Type type = new TypeToken<ArrayList<Restaurant>>() {}.getType();
        favList = gson.fromJson(json, type);
        System.out.println("NOT NULL");

        if(favList == null){
            System.out.println("ITS NULL!");
            favList = new ArrayList<Restaurant>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.mapview:
                intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("searchContent", searchContent);
                startActivity(intent);
                finish();
                break;
            case R.id.searchMenuOption:
                intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void extractSearchContent(){
        Intent intent = getIntent();
        searchContent = intent.getStringExtra("searchContent");
    }

    private void loadSearchContent(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        searchContent = sharedPreferences.getString("searchContent", "");
    }

    private void saveSearchContent(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("searchContent",searchContent);
        editor.apply();
    }

    private void setUpListView() {
        ArrayAdapter<Restaurant> adapter;

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        boolean useSearchList = sharedPreferences.getBoolean("useSearchResults", false);

        if (useSearchList) {
            adapter = new MyListAdapter(true);
        } else {
            adapter = new MyListAdapter();
        }

        ListView list = (ListView) findViewById(R.id.restaurantsListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        Restaurants usedRestaurants;

        public MyListAdapter() {
            super(MainActivity.this, R.layout.restaurants_item_view, restaurants.getList());
            usedRestaurants = restaurants;
        }

        // This constructor is for using the search results instead of the full list.
        public MyListAdapter(boolean useSearchResults) {
            super(MainActivity.this, R.layout.restaurants_item_view, searchRestaurants.getList());
            usedRestaurants = searchRestaurants;
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
                    saveSearchContent();
                    Intent i = RestaurantDetail.makeIntent(MainActivity.this, position);
                    startActivity(i);
                }
            });

            Restaurant restaurant = usedRestaurants.get(position);

            TextView name = (TextView) itemView.findViewById(R.id.RestaurantName);
            name.setText(restaurant.getName());
            name.setTextColor(Color.BLACK);

            for(int i = 0; i < favList.size(); i++){
                if(favList.get(i).getName().equals(restaurant.getName()) && favList.get(i).getPhysicalAddress().equals(restaurant.getPhysicalAddress())){
                    name.setTextColor(Color.rgb(244, 155, 0));
                    System.out.println("COLOR SET!");
                    restaurant.setFavorite(true);
                }
            }

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