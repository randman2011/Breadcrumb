package edu.rosehulman.breadcrumb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by watterlm on 2/6/2015.
 */
public class DBHelper  extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BookmarkDataAdapter.CREATE_STATEMENT);
        db.execSQL(TripDataAdapter.CREATE_STATEMENT);
        db.execSQL(GPSCoordinateDataAdapter.CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TripDataAdapter.DROP_STATEMENT);
        db.execSQL(TripDataAdapter.CREATE_STATEMENT);
        db.execSQL(BookmarkDataAdapter.DROP_STATEMENT);
        db.execSQL(BookmarkDataAdapter.CREATE_STATEMENT);
        db.execSQL(GPSCoordinateDataAdapter.DROP_STATEMENT);
        db.execSQL(GPSCoordinateDataAdapter.CREATE_STATEMENT);
    }
}
