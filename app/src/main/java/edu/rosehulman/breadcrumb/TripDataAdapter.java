package edu.rosehulman.breadcrumb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by watterlm on 1/23/2015.
 */
public class TripDataAdapter {
    public static final String TABLE_NAME = "trips";
    public static final String KEY_ID = "_id";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_DISTANCE = "distance";

    private SQLiteDatabase mDb;
    private TripDBHelper mOpenHelper;

    public TripDataAdapter(Context context){
        mOpenHelper = new TripDBHelper(context);
    }

    public void open(){
        mDb = mOpenHelper.getWritableDatabase();
    }

    public void close(){
        mDb.close();
    }

    private ContentValues getContentValues(Trip trip){

        return null;
    }

    private Trip getTripFromCursor(Cursor cursor){

        return null;
    }

    public Trip addTrip(Trip trip){

        return null;
    }

    public void deleteTrip(Trip trip){
        mDb.delete(TABLE_NAME, KEY_ID + " = " + trip.getId(), null);
    }


    private static class TripDBHelper extends SQLiteOpenHelper {
        private static final String CREATE_STATEMENT;
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

        private static final String DROP_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public TripDBHelper(Context context){
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
