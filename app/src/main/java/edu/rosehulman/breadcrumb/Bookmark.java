package edu.rosehulman.breadcrumb;

import java.util.ArrayList;

/**
 * Created by watterlm on 1/23/2015.
 */
public class Bookmark {
    private int id;
    private String title;
    private String description;
    private ArrayList<String> imageFilenames;
    private GPSCoordinate coordinate;

    public Bookmark(String title, String description, GPSCoordinate coordinate){
        this.title = title;
        this.description = description;
        this.coordinate = coordinate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public ArrayList<String> getImageFilenames() {
        return imageFilenames;
    }

    public void setImageFilenames(ArrayList<String> imageFilenames) {
        this.imageFilenames = imageFilenames;
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
}
