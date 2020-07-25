package com.example.saferestaurants.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.saferestaurants.model.ClusterMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;

// This class is to customize icons for pegs within the map view
public class ClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private IconGenerator iconGenerator;
    private ImageView imageView;
    private int returnedRestaurantID;
    public HashMap<Integer, Marker> makeClusterMap;

    public ClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager, int restaurantID) {
        super(context, map, clusterManager);
        this.iconGenerator = iconGenerator;
        this.imageView = imageView;
        this.returnedRestaurantID = restaurantID;

        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        iconGenerator.setContentView(imageView);
    }

    public HashMap<Integer, Marker> getMarkerClusterMap(){
        return makeClusterMap;
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull ClusterMarker item, @NonNull MarkerOptions markerOptions) {
        imageView.setImageResource(item.getIcon());
        Bitmap icon  = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle()).snippet(item.getSnippet());
    }

    @Override
    protected void onClusterItemRendered(ClusterMarker clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        if(clusterItem.getRestaurantID() == returnedRestaurantID) {
            getMarker(clusterItem).showInfoWindow();
        }
    }

    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<ClusterMarker> cluster) {
        return cluster.getSize() > 6;
    }
}
