package edu.rosehulman.breadcrumb;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by watterlm on 1/23/2015.
 */
public class GPSCoordinate implements Comparable<GPSCoordinate>{
    private double longitude;
    private double latitude;
    private long id;

    public GPSCoordinate(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }


    // For Database
    public GPSCoordinate(){

    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return this.id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public LatLng toLatLong() { return new LatLng(this.latitude, this.longitude);}

    @Override
    public int compareTo(GPSCoordinate another) {
        long otherId = another.getId();
        Long other = Long.valueOf(otherId);
        Long current = Long.valueOf(getId());
        return other.compareTo(current);
    }
}
