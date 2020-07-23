package com.example.saferestaurants.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.saferestaurants.R;
import com.example.saferestaurants.model.ClusterMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

// This class is to customize the Info Window of pegs within the map view //
public class CustomMarkerInfoWindow implements GoogleMap.InfoWindowAdapter {
    private final View markerItemView;
    private Context context;

    public CustomMarkerInfoWindow(Context context) {
        this.context = context;
        markerItemView = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view){
        final ClusterMarker clusterMarker = (ClusterMarker) marker.getTag();

        String restaurantName = marker.getTitle();
        TextView restaurant_Name = markerItemView.findViewById(R.id.restaurant_name);

        if(!restaurantName.equals("")){
            restaurant_Name.setText(restaurantName);
        }

        String restaurantAddressAndHazard = marker.getSnippet();
        TextView restaurant_Address_And_Hazard = markerItemView.findViewById(R.id.restaurant_address_and_hazard);

        if(!restaurantAddressAndHazard.equals("")){
            restaurant_Address_And_Hazard.setText(restaurantAddressAndHazard);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, markerItemView);
        return markerItemView;
    }
    @Override
    public View getInfoContents(Marker marker) {
        return markerItemView;
    }
}
