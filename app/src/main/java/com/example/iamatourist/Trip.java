package com.example.iamatourist;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;

public class Trip {
    private ArrayList<Image> gallery;
    private String title;
    private Date date;
    private Location loc;

    public Trip() {
        this.gallery = new ArrayList<>();
    }

    public Trip(String title, Date date) {
        this.gallery = new ArrayList<>();
        this.title = title;
        this.date = date;
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

    public Image getTopImage(Integer index){
        return gallery.get(index);
    }

    public ArrayList<Image> getGallery() {
        return gallery;
    }

    public Image getImageAtPos(Integer pos) {
        return gallery.get(pos);
    }

    public Integer getGallerySize() {
        return gallery.size();
    }

    public void setGallery(ArrayList<Image> gallery) {
        this.gallery = gallery;
    }
}
