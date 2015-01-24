package edu.rosehulman.breadcrumb;

/**
 * Created by watterlm on 1/23/2015.
 */
public class GPSCoordinate {
    private double longitude;
    private double latitude;

    public GPSCoordinate(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
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
}
