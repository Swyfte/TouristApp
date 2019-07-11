package com.example.iamatourist;

import java.util.ArrayList;
import java.util.Date;

public class Trip {
    private ArrayList<Image> gallery;
    private String title;
    private Date date;

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

    public void setGallery(ArrayList<Image> gallery) {
        this.gallery = gallery;
    }
}