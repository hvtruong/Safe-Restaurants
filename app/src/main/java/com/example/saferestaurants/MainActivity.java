package com.example.saferestaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Fields
    Restaurants restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setData();
        restaurants = Restaurants.getInstance();
        setUpListView();
    }

    private void setData(){

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

    private class MyListAdapter extends ArrayAdapter<Restaurant>{

        public MyListAdapter() {
            //Correct this line later. Jasleen might not call the method that send the arrayList getList
            super(MainActivity.this,R.layout.restaurants_item_view,restaurants.getList());

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Make sure we have a view to work with
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.restaurants_item_view, parent, false);
            }

            //find the Restaurant
            Restaurant restaurant = restaurants.get(position);
            //Fill the view
            TextView name = (TextView) findViewById(R.id.RestaurantName);
            name.setText(restaurant.getName());

            return itemView;
        }
    }

}
