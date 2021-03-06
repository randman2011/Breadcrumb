package edu.rosehulman.breadcrumb;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by watterlm on 1/23/2015.
 */
public class Bookmark implements Comparable<Bookmark>{
    private long id;
    private String title;
    private String description;
    private ArrayList<String> imageURIs;
    private GPSCoordinate coordinate;
    private Calendar lastVisited;

    public Bookmark(String title, String description, GPSCoordinate coordinate, Calendar lastVisted){
        this.title = title;
        this.description = description;
        this.coordinate = coordinate;
        this.lastVisited = lastVisted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(Calendar lastVisted) {
        this.lastVisited = lastVisted;
    }

    public ArrayList<String> getImageURIs() {
        return imageURIs;
    }

    public void setImageURIs(ArrayList<String> imageURIs) {
        this.imageURIs = imageURIs;
    }

    public GPSCoordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(GPSCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void storeImage(String filename){
        //TODO: store Image
    }

    public void removeImage(String filename){
        //TODO: remove Image
    }

    @Override
    public int compareTo(Bookmark another) {
        long otherId = another.getId();
        Long other = Long.valueOf(otherId);
        Long current = Long.valueOf(getId());
        return other.compareTo(current);
    }

    public ArrayList<Bitmap> getBitmapFromUriStrings(Context context){
        return getBitmapFromUriStrings(context, 96);
    }

    public ArrayList<Bitmap> getBitmapFromUriStrings(Context context, int height){
        ArrayList<Bitmap> bmpArrayList = new ArrayList<>();
        float density = context.getResources().getDisplayMetrics().density;
        int pixels =  Math.round((float)height * density);
        for (int i = 0; i < this.imageURIs.size(); i++) {
            Uri uri = null;
            try {
                uri = Uri.parse(this.imageURIs.get(i));
                Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                bmpArrayList.add(Bitmap.createScaledBitmap(bmp, pixels, pixels, true));
            } catch (Exception e) {
                //Toast.makeText(context, e.toString() + " " + uri.toString(), Toast.LENGTH_LONG).show();
            }
        }
        //Toast.makeText(context, "Uris: " + this.imageURIs.size() + " Images: " + bmpArrayList.size(), Toast.LENGTH_LONG).show();
        return bmpArrayList;
    }
}
