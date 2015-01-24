package edu.rosehulman.breadcrumb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by watterlm on 1/23/2015.
 */
public class Trip {
    private int id;
    private Calendar startDate;
    private Calendar endDate;
    private double distance = 0;
    private ArrayList<GPSCoordinate> coordinates;

    public Trip(Calendar date){
        this.startDate = date;
    }

    public Trip(){
        // TODO: for database
    }
    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public double getDistance() {
        if(this.distance == 0 && this.coordinates.size() > 1){
            this.distance = calculateDistanceKM();
            // TODO: Add check for preferences in miles and do miles calculation
        }

        return distance;
    }

    public ArrayList<GPSCoordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<GPSCoordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private double calculateDistanceKM(){
        // Uses the 'haversine' formula to calculate the great-circle distance
        // between all points of the Trip
        // a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
        // c = 2 ⋅ atan2( √a, √(1−a) )
        // d = R ⋅ c
        // where	φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km);
        // note that angles need to be in radians to pass to trig functions!
        double earthsRadius = 6371; // km
        double totalDistance = 0; // km

        for (int i = 1; i < coordinates.size(); i++) {
            GPSCoordinate firstPoint = coordinates.get(i-1);
            GPSCoordinate lastPoint = coordinates.get(i);

            // get the latitude and longitude
            double lat1 = firstPoint.getLatitude();
            double lat2 = lastPoint.getLatitude();
            double long1 = firstPoint.getLongitude();
            double long2 = lastPoint.getLongitude();

            // get phi1, phi2, deltaPhi, and delta lambda
            double phi1 = Math.toRadians(lat1);
            double phi2 = Math.toRadians(lat2);
            double deltaPhi = Math.toRadians(lat2 - lat1);
            double deltaLambda = Math.toRadians(long2 - long1);

            // calculate a, c, and d
            double a = Math.sin(deltaPhi/2.0) * Math.sin(deltaPhi/2.0) + Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda/2.0) * Math.sin(deltaLambda/2.0);
            double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
            double d = earthsRadius * c;

            // update total distance
            totalDistance += d;
        }

        return totalDistance;
    }

    private double getMiles(double distanceKM){
        double milesPerKM = .62137;
        return distanceKM * milesPerKM;
    }

    public int[] calculateDuration(){
        // Define return string
        int[] times = {0, 0, 0};
        // Define conversion units
        long minCon = 60L * 1000L;
        long hourCon = 60L * 60L * 1000L;
        long dayCon = 24L * 60L * 60L * 1000L;
        // Get the milliseconds of each date
        long startMilli = startDate.getTimeInMillis();
        long endMilli = endDate.getTimeInMillis();

        // Do difference
        long timeDiffMilli = endMilli - startMilli;

        // Determine day, hour, and minute difference
        int timeDiffDay = (int)(timeDiffMilli / dayCon);
        if(timeDiffDay >= 1){
            times[0] = timeDiffDay;
            timeDiffMilli = timeDiffMilli - timeDiffDay * dayCon;
        }

        int timeDiffHour = (int)(timeDiffMilli / hourCon);
        if(timeDiffHour >= 1){
            times[1] = timeDiffHour;
            timeDiffMilli = timeDiffMilli - timeDiffHour * hourCon;
        }

        int timeDiffMin = (int) (timeDiffMilli / minCon);
        if(timeDiffMin >= 1){
            times[2] = timeDiffMin;
        }

        return times;
    }

    public double calculateAverageSpeed(int[] times, double distance){
        double hours = times[0]*24.0 + times[1] + times[2]/60.0;
        return distance/hours;
    }
}
