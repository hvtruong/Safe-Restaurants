package com.example.saferestaurants;

import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import org.junit.Test;

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
}
