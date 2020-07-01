package com.example.saferestaurants;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Restaurant;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class RestaurantTest {

    @Test
    public void restaurant(){

        String trackingNumber = "SWOD-AHZUMF";
        String name = "Lee Yuen Seafood Restaurant";
        String address = "14755 104 Ave";
        String city = "Surrey";
        String fact = "Restaurant";
        String latitude = "49.19166808";
        String longitude = "-122.8136896";

        Restaurant restaurant = new Restaurant(trackingNumber, name, address, city, fact, latitude, longitude);

        assertEquals(trackingNumber, restaurant.getTrackingNumber());
        assertEquals(name, restaurant.getName());
        assertEquals(address, restaurant.getPhysicalAddress());
        assertEquals(city, restaurant.getPhysicalCity());
        assertEquals(fact, restaurant.getFactType());
        assertEquals(Double.parseDouble(latitude), restaurant.getLatitude(), 0.01);
        assertEquals(Double.parseDouble(longitude), restaurant.getLongitude(), 0.01);

        String date = "20181024";
        String type = "Follow-Up";
        String criticalIssues = "0";
        String nonCriticalIssues = "1";
        String hazardRating = "Low";
        ArrayList<String> violations = new ArrayList<>();
        violations.add("308,Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat");

        restaurant.addInspection(new Inspection(date, type, criticalIssues, nonCriticalIssues, hazardRating, violations));

        assertEquals(0, restaurant.getInspection().get(0).getCriticalIssues());
    }
}
