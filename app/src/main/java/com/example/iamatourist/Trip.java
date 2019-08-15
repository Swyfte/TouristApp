package com.example.iamatourist;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;

public class Trip {
    private ArrayList<Image> gallery;
    private String title;
    private Date date;
    private String loc;

    /**
     * Default constructor, initialises an empty gallery
     */
    public Trip() {
        this.gallery = new ArrayList<>();
    }

    /**
     * Constructor that creates an empty gallery and also connects the various details that are
     * supplied
     * @param title
     * @param date
     * @param loc
     */
    public Trip(String title, Date date, String loc) {
        this.gallery = new ArrayList<>();
        this.title = title;
        this.date = date;
        this.loc = loc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Ad an image to the gallery.
     * @param img Image to add
     */
    public void addImage(Image img){
        gallery.add(img);
    }

    public ArrayList<Image> getGallery() {
        return gallery;
    }

    /**
     * Used to retrieve an image from the gallery. It is used in the TripAdapter to
     * fill the three ImageViews, provided it's not more than the size of the array
     * @param pos position in the array
     * @return the image at the position
     */
    public Image getImageAtPos(Integer pos) {
        if (pos <= gallery.size()) {
            return gallery.get(pos);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Remove an image from the gallery, assuming it's there
     * @param img The image to remove
     */
    public void removeImage(Image img) {
        gallery.remove(img);
    }

    /**
     * Get the size of the gallery. Used by TripAdapter to prevent out of bounds exceptions
     * @return size of the gallery
     */
    public Integer getGallerySize() {
        return gallery.size();
    }

    public void setGallery(ArrayList<Image> gallery) {
        this.gallery = gallery;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
