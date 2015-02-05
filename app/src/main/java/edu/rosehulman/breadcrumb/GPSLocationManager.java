package edu.rosehulman.breadcrumb;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by watterlm on 2/5/2015.
 */
public class GPSLocationManager implements LocationListener {

    private LocationManager locationManager;
    private String locationProvider;
    private Context mContext;
    private int tripID = -1;

    public GPSLocationManager(Context context) {
        this.mContext = context;
        this.locationProvider = LocationManager.NETWORK_PROVIDER;
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
    }

    public GPSLocationManager(Context context, int tripID) {
        this(context);
        this.tripID = tripID;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getCurrentLocation(){
        Location location = locationManager.getLastKnownLocation(locationProvider);
        return location;
    }

    public void endTracking(){
        locationManager.removeUpdates(this);
    }

    public void startTracking(){
        locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
    }
}
