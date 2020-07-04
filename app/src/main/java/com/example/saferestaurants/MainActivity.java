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

import java.io.FileNotFoundException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Fields
    Restaurants restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Parsing Data Files //
        try {
            DataParser.parseRestaurants("src/main/java/com/example/saferestaurants/ProjectData/restaurants_itr1.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            DataParser.parseInspections("src/main/java/com/example/saferestaurants/ProjectData/inspectionreports_itr1.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //                   //

        restaurants = Restaurants.getInstance();
        setUpListView();

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
