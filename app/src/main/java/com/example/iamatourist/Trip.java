package com.example.iamatourist;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;

public class Trip {
    private ArrayList<Image> gallery;
    private String title;
    private Date date;
    private String loc;

    public Trip() {
        this.gallery = new ArrayList<>();
    }

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

    public void addImage(Image img){
        gallery.add(img);
    }

    public ArrayList<Image> getGallery() {
        return gallery;
    }

    public Image getImageAtPos(Integer pos) {
        return gallery.get(pos);
    }

    public void removeImage(Image img) {
        gallery.remove(img);
    }

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
