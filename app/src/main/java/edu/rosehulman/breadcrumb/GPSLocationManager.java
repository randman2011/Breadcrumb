package edu.rosehulman.breadcrumb;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

/**
 * Created by watterlm on 2/5/2015.
 */
public class GPSLocationManager implements LocationListener {

    private LocationManager locationManager;
    private String locationProvider;
    private Context mContext;
    private Trip trip;

    public GPSLocationManager(Context context) {
        this.mContext = context;
        this.locationProvider = LocationManager.NETWORK_PROVIDER;
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (trip != null) {
            trip.addCoordinate(new GPSCoordinate(location.getLatitude(), location.getLongitude()));
        }
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

    public LatLng getCurrentLocation(){
        Location location = locationManager.getLastKnownLocation(locationProvider);
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public GPSCoordinate getGPSCoordinate() {
        Location location = locationManager.getLastKnownLocation(locationProvider);
        return new GPSCoordinate(location.getLatitude(), location.getLongitude());
    }

    public Trip endTracking(){
        locationManager.removeUpdates(this);
        return trip;
    }

    public void startTracking(Trip trip, GoogleMap mMap){
        locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
        this.trip = new Trip(Calendar.getInstance());
    }
}
