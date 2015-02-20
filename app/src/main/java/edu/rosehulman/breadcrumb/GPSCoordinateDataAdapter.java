package edu.rosehulman.breadcrumb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by watterlm on 1/23/2015.
 */
public class GPSCoordinateDataAdapter {
    private static final String TABLE_NAME = "gps_coordinates";
    private static final String KEY_ID = "_id";
    private static final String KEY_TRIP_ID = "trip_id";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";

    public static final String CREATE_STATEMENT;
    static{
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(TABLE_NAME);
        sb.append(" (");
        sb.append(KEY_ID + " integer primary key autoincrement, ");
        sb.append(KEY_TRIP_ID + " integer, ");
        sb.append(KEY_LONGITUDE + " double, ");
        sb.append(KEY_LATITUDE + " double, ");
        sb.append("foreign key (");
        sb.append(KEY_TRIP_ID + ") references ");
        sb.append(TripDataAdapter.TABLE_NAME + "(");
        sb.append(TripDataAdapter.KEY_ID + ")");
        sb.append(")");
        CREATE_STATEMENT = sb.toString();
    }

    public static final String DROP_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;


    private SQLiteDatabase mDb;
    private DBHelper mOpenHelper;

    public GPSCoordinateDataAdapter(Context context){
        mOpenHelper = new DBHelper(context);
    }

    public void open(){
        mDb = mOpenHelper.getWritableDatabase();
    }

    public void close(){
        mDb.close();
    }

    private ContentValues getContentValues(GPSCoordinate coordinate){
        ContentValues row = new ContentValues();
        row.put(KEY_LATITUDE, coordinate.getLatitude());
        row.put(KEY_LONGITUDE, coordinate.getLongitude());
        return row;
    }

    private GPSCoordinate getGPSCoordinateFromCursor(Cursor cursor){
        GPSCoordinate coordinate = new GPSCoordinate();
        coordinate.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
        coordinate.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE)));
        coordinate.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE)));
        coordinate.setTripId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TRIP_ID)));
        return coordinate;
    }

    public ArrayList<GPSCoordinate> getAllCoordinates(ArrayList<GPSCoordinate> coordinates, long tripID){
        coordinates.clear();
        String[] projection = new String[] { KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_TRIP_ID};
        String selection = KEY_TRIP_ID + " = " + tripID;
        Cursor cursor = mDb.query(TABLE_NAME, projection, selection, null, null, null, null, null);
        if (!cursor.moveToFirst()){
            return coordinates;
        }
        do {
            GPSCoordinate toAdd = getGPSCoordinateFromCursor(cursor);
            coordinates.add(toAdd);
        } while (cursor.moveToNext());
        Collections.sort(coordinates);
        return coordinates;
    }

    public void addGPSCoordinates(ArrayList<GPSCoordinate> coordinates, long tripID){
        for (GPSCoordinate coordinate : coordinates ) {
            ContentValues row = getContentValues(coordinate);
            row.put(KEY_TRIP_ID, tripID);
            long rowId = mDb.insert(TABLE_NAME, null, row);
            coordinate.setId(rowId);
        }
    }

    public long getTripIdForBookmarkUpdate(double latitude, double longitude){
        ArrayList<GPSCoordinate> coordinates = new ArrayList<GPSCoordinate>();
        String[] projection = new String[] { KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_TRIP_ID};
        String selection = "abs(" + KEY_LATITUDE + " - " + latitude + ") <= 10 AND abs(" + KEY_LONGITUDE + " - " + longitude + ") <= 10";
        Cursor cursor = mDb.query(TABLE_NAME, projection, selection, null, null, null, null, null);
        if (!cursor.moveToFirst()){
            return 0L;
        }
        do {
            GPSCoordinate toAdd = getGPSCoordinateFromCursor(cursor);
            coordinates.add(toAdd);
        } while (cursor.moveToNext());
        Collections.sort(coordinates);

        if (coordinates.size() > 1){
            double distance = 10;
            GPSCoordinate closestCoordinate = null;
            for (GPSCoordinate coord: coordinates){
                double currentDis = getDistance(coord, latitude, longitude);
                if (currentDis <= distance){
                    distance = currentDis;
                    closestCoordinate = coord;
                }
            }
            if(closestCoordinate != null){
                return closestCoordinate.getTripId();
            }else{
                return 0L;
            }
        }else{
            return coordinates.get(0).getTripId();
        }
    }

    public double getDistance(GPSCoordinate coord, double latitude, double longitude){
        double distance = Math.sqrt(Math.pow(latitude - coord.getLatitude(), 2) + Math.pow(longitude - coord.getLongitude(), 2));
        return distance;
    }

    public void deleteGPSCoordinates(long tripID){
        mDb.delete(TABLE_NAME, KEY_TRIP_ID + " = " + tripID, null);
    }
}
