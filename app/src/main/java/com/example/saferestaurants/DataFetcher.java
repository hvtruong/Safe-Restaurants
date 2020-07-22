package com.example.saferestaurants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;



public class DataFetcher {

    public final static String inspectionDatabaseURL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    public final static String inspectionFileName = "inspectionreports_fetched.csv";

    public final static String restaurantDatabaseURL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    public final static String restaurantFileName = "restaurants_fetched.csv";

    // Get the calling activity to set this variable with getFilesDir().
    private static String fileLocation = null;
    private static String inspectionDataURL = null;
    private static String restaurantDataURL = null;

    public static String fetchDataURL(String urlString) {
        /* Obtain the URL for the most recent list of inspections. */
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            InputStream raw = urlConnection.getInputStream();
            InputStream buffer = new BufferedInputStream(raw);

            BufferedReader reader = new BufferedReader(new InputStreamReader(buffer));
            String line = reader.readLine();

            // Checking that the JSON string we read from the server isn't empty.
            assert line != null;

            // Dig through the JSON file to find the download URL for the relevant data.
            JSONObject json = new JSONObject(line);
            json = new JSONObject(json.get("result").toString());
            JSONArray array = new JSONArray(json.get("resources").toString());
            json = new JSONObject(array.get(0).toString());
            return json.get("url").toString();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fetchRestaurantData() {
        /* Download restaurant list CSV file. */

        assert fileLocation != null;

        // Get updated URL for restaurant file before downloading.
        restaurantDataURL = fetchDataURL(restaurantDatabaseURL);


    }

    public String fetchData(String urlString) {
        /* Download inspection list CSV file. */

        assert fileLocation != null;

        // Get updated URL for inspection file before downloading.
        String dataURL = fetchDataURL(urlString);

        String fileName;
        if (dataURL.contains("restaurants.csv")) {
            restaurantDataURL = new String(dataURL);
            fileName = restaurantFileName;
        } else if (dataURL.contains("inspectionreports.csv")) {
            inspectionDataURL = new String(dataURL);
            fileName = inspectionFileName;
        } else {
            throw new RuntimeException();
        }

        // Following code taken from https://www.baeldung.com/java-download-file#using-java-io
        try (BufferedInputStream in = new BufferedInputStream(new URL(dataURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileLocation + "/" + fileName)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // End of repurposed code.
        return dataURL;

    }

    public static void setFileLocation(String location) {
        fileLocation = location;
    }
    public static String getInspectionFileName() {
        return inspectionFileName;
    }


    // Currently, the DataParser can't handle the orientation of data in the new CSV file.
    // Either the parser needs to be amended, or the data itself must be manipulated.
    // The former seems to be the method that will give the smallest headache.


}