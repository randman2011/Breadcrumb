package edu.rosehulman.breadcrumb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by watterlm on 1/23/2015.
 */
public class TripDataAdapter {
    public static final String TABLE_NAME = "trips";
    public static final String KEY_ID = "_id";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_DISTANCE = "distance";

    public static final String CREATE_STATEMENT;
    static{
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(TABLE_NAME);
        sb.append(" (");
        sb.append(KEY_ID + " integer primary key autoincrement, ");
        sb.append(KEY_START_DATE + " text, ");
        sb.append(KEY_END_DATE + " text, ");
        sb.append(KEY_DISTANCE + " double");
        sb.append(")");
        CREATE_STATEMENT = sb.toString();
    }

    public static final String DROP_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase mDb;
    private DBHelper mOpenHelper;
    private GPSCoordinateDataAdapter mGPSAdapter;

    public TripDataAdapter(Context context){
        mOpenHelper = new DBHelper(context);
        mGPSAdapter = new GPSCoordinateDataAdapter(context);
        mGPSAdapter.open();
    }

    public void open(){
        mDb = mOpenHelper.getWritableDatabase();
    }

    public void close(){
        mDb.close();
    }

    private ContentValues getContentValues(Trip trip){
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        ContentValues row = new ContentValues();
        row.put(KEY_START_DATE, simpleFormat.format(trip.getStartDate().getTime()));
        row.put(KEY_END_DATE, simpleFormat.format(trip.getEndDate().getTime()));
        row.put(KEY_DISTANCE, trip.getDistance());
        return row;
    }

    public ArrayList<Trip> getAllTrips(ArrayList<Trip> trips){
        trips.clear();
        Cursor cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null, null);
        if (!cursor.moveToFirst()){
            return trips;
        }
        do {
            Trip toAdd = getTripFromCursor(cursor);
            trips.add(toAdd);
        } while (cursor.moveToNext());
        Collections.sort(trips);
        return trips;
    }

    public Trip getTrip(long id){
        String[] projection = new String[] { KEY_ID, KEY_START_DATE, KEY_END_DATE, KEY_DISTANCE };
        String selection = KEY_ID + " = " + id;
        Cursor c = mDb.query(TABLE_NAME, projection, selection, null, null, null, null, null);
        if (c != null && c.moveToFirst()){
            Trip trip = getTripFromCursor(c);
            trip.setCoordinates(mGPSAdapter.getAllCoordinates(trip.getCoordinates(), trip.getId()));
            return trip;
        }
        return null;
    }

    private Trip getTripFromCursor(Cursor cursor){
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        Trip trip = new Trip();
        try {
            Date endDate = simpleFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(KEY_END_DATE)));
            Date startDate = simpleFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(KEY_START_DATE)));

            trip.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));

            Calendar end = Calendar.getInstance();
            end.setTime(endDate);
            trip.setEndDate(end);

            Calendar start = Calendar.getInstance();
            start.setTime(startDate);
            trip.setStartDate(start);

            trip.setDistance(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_DISTANCE)));

            trip.setCoordinates(mGPSAdapter.getAllCoordinates(trip.getCoordinates(), trip.getId()));
        } catch (ParseException e) {
            return null;
        }
        return trip;
    }

    public long addTrip(Trip trip){
        ContentValues row = getContentValues(trip);
        long rowId = mDb.insert(TABLE_NAME, null, row);
        trip.setId(rowId);
        mGPSAdapter.addGPSCoordinates(trip.getCoordinates(), rowId);
        return rowId;
    }

    public void deleteTrip(Trip trip){
        mDb.delete(TABLE_NAME, KEY_ID + " = " + trip.getId(), null);
        mGPSAdapter.deleteGPSCoordinates(trip.getId());
    }

}
