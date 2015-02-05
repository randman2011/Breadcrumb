package edu.rosehulman.breadcrumb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by watterlm on 1/23/2015.
 */
public class BookmarkDataAdapter {
    private static final String TABLE_NAME = "bookmark";
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE_FILENAMES = "images";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LAST_VISITED ="last_visited";

    private SQLiteDatabase mDb;
    private BookmarkDBHelper mOpenHelper;

    public BookmarkDataAdapter(Context context){
        mOpenHelper = new BookmarkDBHelper(context);
    }

    public void open(){
        mDb = mOpenHelper.getWritableDatabase();
    }

    public void close(){
        mDb.close();
    }

    private ContentValues getContentValues(Bookmark bookmark){
        ContentValues row = new ContentValues();
        row.put(KEY_TITLE, bookmark.getTitle());
        row.put(KEY_DESCRIPTION, bookmark.getDescription());
        row.put(KEY_IMAGE_FILENAMES, (Constants.constants.serialize((String[])bookmark.getImageFilenames().toArray())));
        row.put(KEY_LONGITUDE, bookmark.getCoordinate().getLongitude());
        row.put(KEY_LATITUDE, bookmark.getCoordinate().getLatitude());
        row.put(KEY_LAST_VISITED, bookmark.getLastVisted().getTimeInMillis());

        return null;
    }

    private Bookmark getBookmarkFromCursor(Cursor cursor){
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION));
        String[] images = Constants.constants.deserialize(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_FILENAMES)));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE));
        GPSCoordinate coord = new GPSCoordinate(longitude, latitude);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LAST_VISITED)));
        Bookmark bookmark = new Bookmark(title, description, coord, cal);
        bookmark.setImageFilenames(new ArrayList<String>(Arrays.asList(images)));
        bookmark.setId(id);
        return bookmark;
    }

    public long addBookmark(Bookmark bookmark){
        ContentValues row = getContentValues(bookmark);
        long id = mDb.insert(TABLE_NAME, null, row);
        bookmark.setId(id);
        return id;
    }

    public void deleteBookmark(Bookmark bookmark){
        mDb.delete(TABLE_NAME, KEY_ID + " = " + bookmark.getId(), null);
    }

    private static class BookmarkDBHelper extends SQLiteOpenHelper {
        private static final String CREATE_STATEMENT;

        static {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE ");
            sb.append(TABLE_NAME);
            sb.append(" (");
            sb.append(KEY_ID + " integer primary key autoincrement, ");
            sb.append(KEY_TITLE + " text, ");
            sb.append(KEY_DESCRIPTION + " text, ");
            sb.append(KEY_IMAGE_FILENAMES + " text, ");
            sb.append(KEY_LONGITUDE + " double, ");
            sb.append(KEY_LATITUDE + " double, ");
            sb.append(KEY_LAST_VISITED + " long ");
            sb.append(")");
            CREATE_STATEMENT = sb.toString();
        }

        private static final String DROP_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public BookmarkDBHelper(Context context) {
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
