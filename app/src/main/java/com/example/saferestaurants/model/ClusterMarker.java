package com.example.saferestaurants.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem{
    private LatLng position;
    private String title;
    private String snippet;
    private String hazardLevel;
    private int icon;
    private int restaurantID;

    public ClusterMarker(LatLng position, String title, String snippet, int icon, int restaurantID) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.icon = icon;
        this.restaurantID = restaurantID;
    }

    public ClusterMarker() {
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }
}
