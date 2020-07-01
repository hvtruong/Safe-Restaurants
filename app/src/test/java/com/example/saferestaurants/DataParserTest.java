package com.example.saferestaurants;

import com.example.saferestaurants.model.Restaurants;

import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class DataParserTest {
    @Test
    public void dataParser() throws FileNotFoundException {
        DataParser.parseRestaurants("src/main/java/com/example/saferestaurants/ProjectData/restaurants_itr1.csv");
        DataParser.parseInspections("src/main/java/com/example/saferestaurants/ProjectData/inspectionreports_itr1.csv");

        Restaurants restaurants = Restaurants.getInstance();
        assertEquals(8, restaurants.size());
    }
}
