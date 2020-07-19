package com.example.saferestaurants;

import android.util.Log;

import com.example.saferestaurants.model.Inspection;
import com.example.saferestaurants.model.Restaurant;
import com.example.saferestaurants.model.Restaurants;

import java.io.BufferedReader;

import java.io.IOException;
import java.util.ArrayList;

/*
DataParser class parses the data file and sets up the Restaurants object with that data.
 */
public class DataParser {
    public static void parseRestaurants(BufferedReader reader) {

        try {
            reader.readLine(); // removing top line of column descriptions
        } catch (IOException e) {
            Log.wtf("MainActivity", "Error reading data file on line", e);
            e.printStackTrace();
        }

        Restaurants restaurants = Restaurants.getInstance();
        String line = "";

        try {
            while((line = reader.readLine()) != null){
                String restaurantName;
                String[] restaurantAttributes;
                if (line.contains("\"")) {
                    restaurantName = line.split("\"")[1];
                    String lineFix = line.replace("\"" + restaurantName + "\"", "");
                    restaurantAttributes = lineFix.split(",");
                } else {
                    restaurantName = "";
                    restaurantAttributes = line.split(",");
                }

                if (restaurantAttributes[1].equals("")) {
                    restaurantAttributes[1] = new String(restaurantName);
                }

                Restaurant restaurant = new Restaurant(
                        restaurantAttributes[0].replace("\"", ""),     // tracking Number
                        restaurantAttributes[1].replace("\"", ""),     // name
                        restaurantAttributes[2].replace("\"", ""),     // physical address
                        restaurantAttributes[3].replace("\"", ""),     // physical city
                        restaurantAttributes[4].replace("\"", ""),     // factype
                        restaurantAttributes[5],                                           // latitude
                        restaurantAttributes[6]                                            // longitude
                );
                restaurants.add(restaurant);
            }
        } catch (IOException e) {
            Log.wtf("MainActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

    }

    public static void parseInspections(BufferedReader reader) {

        try {
            reader.readLine(); // removing top line of column descriptions
        } catch (IOException e) {
            Log.wtf("MainActivity", "Error reading data file on line", e);
            e.printStackTrace();
        }

        Restaurants restaurants = Restaurants.getInstance();
        String line = "";

        try {
            while((line = reader.readLine()) != null){
                // Format from Fraser Health inspection lists:
                // {TRACKINGNUMBER},{INSPECTIONDATE},{INSPTYPE},{NUMCRITICAL},{VIOLLUMP},{HAZARDRATING}

                String[] inspectionAttributes = new String[7];
                String[] rawInspectionAttributes = line.split(",");

                if (rawInspectionAttributes.length == 0) {
                    break;
                }

                /*
                // x = last character in the string.
                char x = line.charAt(line.length()-1);

                if(x == ',') { // if a line ends in a comma then it has no violations
                    inspectionAttributes = line.split(",", 6);
                    inspectionAttributes[5] = inspectionAttributes[5].replaceAll(",", "");
                } else {
                    inspectionAttributes = line.split(",", 7);
                }

                 */

                // Separate the data into elements of a string array.
                for (int i = 0; i < 5; i++) {
                    inspectionAttributes[i] = rawInspectionAttributes[i];
                }
                inspectionAttributes[6] = rawInspectionAttributes[rawInspectionAttributes.length - 1];

                // Make sure the array has violations before attempting to access them.
                if (line.indexOf('\"') != -1) {
                    inspectionAttributes[5] = line.split("\"")[1];
                }

                ArrayList<String> violations = new ArrayList<>();

                if (inspectionAttributes[5] != null) {
                    String[] violationsSplit = inspectionAttributes[5].replace("\"", "").split("\\|");
                    for(String violation: violationsSplit){
                        violations.add(violation);
                    }
                }
                Inspection inspection = new Inspection(
                        inspectionAttributes[1],                                        // date
                        inspectionAttributes[2].replace("\"", ""),  // type
                        inspectionAttributes[3],                                        // critical issues
                        inspectionAttributes[4],                                        // non critical issues
                        inspectionAttributes[6].replace("\"", ""),  // hazard rating
                        violations                                                      // violations
                );
                int index = restaurants.find(inspectionAttributes[0].replace("\"", ""));
                restaurants.get(index).addInspection(inspection);



            }
        } catch (IOException e) {
            Log.wtf("MainActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }

    public static void parseInspectionsIteration1(BufferedReader reader){
        try {
            reader.readLine(); // removing top line of column descriptions
        } catch (IOException e) {
            Log.wtf("MainActivity", "Error reading data file on line", e);
            e.printStackTrace();
        }

        Restaurants restaurants = Restaurants.getInstance();
        String line = "";

        try {
            while((line = reader.readLine()) != null){

                String[] inspectionAttributes;

                char x = line.charAt(line.length()-1);

                if(x == ','){ // if a line ends in a comma then it has no violations
                    inspectionAttributes = line.split(",", 6);
                    inspectionAttributes[5] = inspectionAttributes[5].replaceAll(",", "");
                } else {
                    inspectionAttributes = line.split(",", 7);
                }

                ArrayList<String> violations = new ArrayList<>();

                if(inspectionAttributes.length == 7){
                    String[] violationsSplit = inspectionAttributes[6].replace("\"", "").split("\\|");
                    for(String violation: violationsSplit){
                        violations.add(violation);
                    }
                }
                Inspection inspection = new Inspection(
                        inspectionAttributes[1],                                        // date
                        inspectionAttributes[2].replace("\"", ""),  // type
                        inspectionAttributes[3],                                        // critical issues
                        inspectionAttributes[4],                                        // non critical issues
                        inspectionAttributes[5].replace("\"", ""),  // hazard rating
                        violations                                                      // violations
                );
                int index = restaurants.find(inspectionAttributes[0].replace("\"", ""));
                restaurants.get(index).addInspection(inspection);

            }
        } catch (IOException e) {
            Log.wtf("MainActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }


    }

    public static void parseRestaurantsIteration1(BufferedReader reader){
        try {
            reader.readLine(); // removing top line of column descriptions
        } catch (IOException e) {
            Log.wtf("MainActivity", "Error reading data file on line", e);
            e.printStackTrace();
        }

        Restaurants restaurants = Restaurants.getInstance();
        String line = "";

        try {
            while((line = reader.readLine()) != null){

                String[] restaurantAttributes = line.split(",");
                Restaurant restaurant = new Restaurant(
                        restaurantAttributes[0].replace("\"", ""),     // tracking Number
                        restaurantAttributes[1].replace("\"", ""),     // name
                        restaurantAttributes[2].replace("\"", ""),     // physical address
                        restaurantAttributes[3].replace("\"", ""),     // physical city
                        restaurantAttributes[4].replace("\"", ""),     // factype
                        restaurantAttributes[5],                                           // latitude
                        restaurantAttributes[6]                                            // longitude
                );
                restaurants.add(restaurant);
            }
        } catch (IOException e) {
            Log.wtf("MainActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

    }
}
