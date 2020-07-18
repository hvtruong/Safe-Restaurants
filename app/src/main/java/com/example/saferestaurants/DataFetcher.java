package com.example.saferestaurants;

import android.os.AsyncTask;

import java.net.*;
import java.io.*;
import org.json.*;

public class DataFetcher {

    private final static String inspectionDatabaseURL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private final static String inspectionFileName = "inspectionreports_itr2.csv";

    // Get the calling activity to set this variable with getFilesDir().
    private static String fileLocation;

    private static String inspectionDataURL = null;

    static class RetrieveData extends AsyncTask<Void, Void, Void> {

        // Asynchronously fetch inspection data.
        @Override
        protected Void doInBackground(Void... voids) {
            fetchInspectionData();
            return null;
        }

    }

    public static void setFileLocation(String location) {
        fileLocation = location;
    }

    public static String getInspectionFileName() {
        return inspectionFileName;
    }

    public static String fetchInspectionDataURL() {
        try {
            URL url = new URL(inspectionDatabaseURL);
            URLConnection urlConnection = url.openConnection();
            InputStream raw = urlConnection.getInputStream();
            InputStream buffer = new BufferedInputStream(raw);

            BufferedReader reader = new BufferedReader(new InputStreamReader(buffer));
            String line = reader.readLine();

            // Checking that the JSON string we read from the server isn't empty.
            assert line != null;

            // Dig through the JSON file to find the download URL for the most recent inspection.
            JSONObject json = new JSONObject(line);
            json = new JSONObject(json.get("result").toString());
            JSONArray array = new JSONArray(json.get("resources").toString());
            json = new JSONObject(array.get(0).toString());
            return json.get("url").toString();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fetchInspectionData() {
        inspectionDataURL = fetchInspectionDataURL();

        // Following code taken from https://www.baeldung.com/java-download-file#using-java-io
        System.out.println(fileLocation + "/" + inspectionFileName);
        try (BufferedInputStream in = new BufferedInputStream(new URL(inspectionDataURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileLocation + "/" + inspectionFileName)) {
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

    // Currently, the DataParser can't handle the orientation of data in the new CSV file.
    // Either the parser needs to be amended, or the data itself must be manipulated.
    // The former seems to be the method that will give the smallest headache.


}
