package com.example.saferestaurants;

import android.os.AsyncTask;

import java.net.*;
import java.io.*;
import org.json.*;

public class DataFetcher {

    private final static String inspectionDatabaseURL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private final static String inspectionFileName = "inspectionreports_itr2.csv";

    private final static String restaurantDatabaseURL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final static String restaurantFileName = "restaurants_itr2.csv";

    // Get the calling activity to set this variable with getFilesDir().
    private static String fileLocation = null;
    private static String inspectionDataURL = null;
    private static String restaurantDataURL = null;

    static class RetrieveData extends AsyncTask<Void, Void, Void> {
        // Asynchronously fetch inspection data.
        @Override
        protected Void doInBackground(Void... voids) {
            fetchData(inspectionDatabaseURL);
            fetchData(restaurantDatabaseURL);
            return null;
        }
    }

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

    public static void fetchData(String urlString) {
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
