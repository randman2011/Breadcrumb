package edu.rosehulman.breadcrumb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    private SQLiteDatabase mDb;
    private GPSCoordinateDBHelper mOpenHelper;

    public GPSCoordinateDataAdapter(Context context){
        mOpenHelper = new GPSCoordinateDBHelper(context);
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
        return null;
    }

    private GPSCoordinate getGPSCoordinateFromCursor(Cursor cursor){
        GPSCoordinate coordinate = new GPSCoordinate();
        coordinate.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
        coordinate.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE)));
        coordinate.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE)));
        return coordinate;
    }

    public ArrayList<GPSCoordinate> getAllCoordinates(ArrayList<GPSCoordinate> coordinates, long tripID){
        coordinates.clear();
        String[] projection = new String[] { KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_TRIP_ID};
        String selection = KEY_TRIP_ID + " + " + tripID;
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

    public void deleteGPSCoordinates(long tripID){
        mDb.delete(TABLE_NAME, KEY_TRIP_ID + " = " + tripID, null);
    }

    private static class GPSCoordinateDBHelper extends SQLiteOpenHelper {
        private static final String CREATE_STATEMENT;
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

        private static final String DROP_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public GPSCoordinateDBHelper(Context context){
            super(context, Constants.constants.DATABASE_NAME, null, Constants.constants.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_STATEMENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_STATEMENT);
            db.execSQL(CREATE_STATEMENT);
        }
    }
}
