package edu.rosehulman.breadcrumb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

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

    public static final String CREATE_STATEMENT;

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
        sb.append(KEY_LAST_VISITED + " long");
        sb.append(")");
        CREATE_STATEMENT = sb.toString();
    }

    public static final String DROP_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase mDb;
    private DBHelper mOpenHelper;
    private Context context;

    public BookmarkDataAdapter(Context c){
        context = c;
        mOpenHelper = new DBHelper(c);
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
        ArrayList<String> imageURIs = bookmark.getImageURIs();
        if (imageURIs != null) {
            row.put(KEY_IMAGE_FILENAMES, (Constants.serialize((String[]) bookmark.getImageURIs().toArray(new String[bookmark.getImageURIs().size()]))));
        } else {
            row.put(KEY_IMAGE_FILENAMES, "");
        }
        row.put(KEY_LONGITUDE, bookmark.getCoordinate().getLongitude());
        row.put(KEY_LATITUDE, bookmark.getCoordinate().getLatitude());
        row.put(KEY_LAST_VISITED, bookmark.getLastVisited().getTimeInMillis());

        return row;
    }

    private Bookmark getBookmarkFromCursor(Cursor cursor){
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION));
        String[] images = Constants.deserialize(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_FILENAMES)));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE));
        GPSCoordinate coord = new GPSCoordinate(latitude,longitude);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LAST_VISITED)));
        Bookmark bookmark = new Bookmark(title, description, coord, cal);
        bookmark.setImageURIs(new ArrayList<String>(Arrays.asList(images)));
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

    public ArrayList<Bookmark> getAllBookmarks(ArrayList<Bookmark> bookmarks){
        bookmarks.clear();
        Cursor cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null, null);
        if (!cursor.moveToFirst()){
            return bookmarks;
        }
        do {
            Bookmark toAdd = getBookmarkFromCursor(cursor);
            bookmarks.add(toAdd);
        } while (cursor.moveToNext());
        Collections.sort(bookmarks);
        return bookmarks;
    }

    public Bookmark getBookmark(long id){
        String[] projection = new String[] { KEY_ID, KEY_DESCRIPTION, KEY_TITLE, KEY_LATITUDE, KEY_LONGITUDE, KEY_LAST_VISITED, KEY_IMAGE_FILENAMES };
        String selection = KEY_ID + " + " + id;
        Cursor c = mDb.query(TABLE_NAME, projection, selection, null, null, null, null, null);
        if (c != null && c.moveToFirst()){
            return getBookmarkFromCursor(c);
        }
        return null;
    }
}
