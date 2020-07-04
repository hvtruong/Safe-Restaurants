package com.example.saferestaurants;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Inspections;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestaurantsTest {
    @Test
    public void restaurants() {

        String trackingNumber = "SWOD-AHZUMF";
        String name = "Lee Yuen Seafood Restaurant";
        String address = "14755 104 Ave";
        String city = "Surrey";
        String fact = "Restaurant";
        String latitude = "49.19166808";
        String longitude = "-122.8136896";

        Restaurants restaurants = new Restaurants();
        restaurants.add(new Restaurant(trackingNumber, name, address, city, fact, latitude, longitude));

        String name1 = "Pattullo A&W";
        restaurants.add(new Restaurant(trackingNumber, name1, address, city, fact, latitude, longitude));

        String name2 = "104 Sushi & Co.";
        restaurants.add(new Restaurant(trackingNumber, name2, address, city, fact, latitude, longitude));

        String name3 = "Top In Town Pizza";
        restaurants.add(new Restaurant(trackingNumber, name3, address, city, fact, latitude, longitude));

        assertEquals(trackingNumber, restaurants.get(0).getTrackingNumber());
        assertTrue(Restaurants.getInstance() != null);

        assertEquals(name2, restaurants.get(0).getName());
        assertEquals(name, restaurants.get(1).getName());
        assertEquals(name1, restaurants.get(2).getName());
        assertEquals(name3, restaurants.get(3).getName());
    }

    @Test
    public void getList(){
        String trackingNumber = "SWOD-AHZUMF";
        String name = "Lee Yuen Seafood Restaurant";
        String address = "14755 104 Ave";
        String city = "Surrey";
        String fact = "Restaurant";
        String latitude = "49.19166808";
        String longitude = "-122.8136896";

        Restaurants restaurants = new Restaurants();
        restaurants.add(new Restaurant(trackingNumber, name, address, city, fact, latitude, longitude));

        String name1 = "Pattullo A&W";
        restaurants.add(new Restaurant(trackingNumber, name1, address, city, fact, latitude, longitude));

        String name2 = "104 Sushi & Co.";
        restaurants.add(new Restaurant(trackingNumber, name2, address, city, fact, latitude, longitude));

        String date = "20181024";
        String type = "Follow-Up";
        String criticalIssues = "0";
        String nonCriticalIssues = "1";
        String hazardRating = "Low";
        ArrayList<String> violations = new ArrayList<>();
        violations.add("308,Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat");

        restaurants.get(0).addInspection(new Inspection(date, type, criticalIssues, nonCriticalIssues, hazardRating, violations));
        restaurants.get(1).addInspection(new Inspection(date, type, criticalIssues, nonCriticalIssues, hazardRating, violations));
        restaurants.get(2).addInspection(new Inspection(date, type, criticalIssues, nonCriticalIssues, hazardRating, violations));

        ArrayList<Restaurant> copy = restaurants.getList();

        assertEquals(3, copy.size());

    }
}
