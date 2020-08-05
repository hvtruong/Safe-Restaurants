package com.example.saferestaurants.model;

/* Restaurant class holds the information for a single restaurant including its
    inspections, tracking number, name, address, city, fact type, latitude and longitude
 */
public class Restaurant {

    // attributes //
    private String trackingNumber;
    private String name;
    private String physicalAddress;
    private String physicalCity;
    private String factType;
    private double latitude;
    private double longitude;
    private Inspections inspection;
    private boolean isFavorite = false;

    // constructor //
    public Restaurant(String trackingNumber,
                      String name,
                      String physicalAddress,
                      String physicalCity,
                      String factType,
                      String latitude,
                      String longitude){
        this.trackingNumber = trackingNumber;
        this.name = name;
        this.physicalAddress = physicalAddress;
        this.physicalCity = physicalCity;
        this.factType = factType;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.inspection = new Inspections();
    }

    // getters //
    public String getTrackingNumber(){
        return this.trackingNumber;
    }
    public String getName(){
        return this.name;
    }
    public String getPhysicalAddress(){
        return this.physicalAddress;
    }
    public String getPhysicalCity(){
        return this.physicalCity;
    }
    public String getFactType(){
        return this.factType;
    }
    public double getLatitude(){
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public Inspections getInspection(){
        return this.inspection;
    }

    // 'setter' //
    public void addInspection(Inspection inspection){
        this.inspection.add(inspection);
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
