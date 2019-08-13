package com.example.iamatourist;

import android.graphics.Bitmap;
import android.location.Location;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class Image {
    private String title;
    private String desc;
    private Bitmap photo;
    private ArrayList<String> tags = new ArrayList<>();
    private boolean fav;
    private Date date;
    private Date time;
    private Location location;


    public Image() {
        this.fav = false;
    }

    public Image(String title, String desc, Bitmap photo, ArrayList<String> tags) {
        this.fav = false;
        this.title = title;
        this.desc = desc;
        this.photo = photo;
        this.tags = tags;
    }

    public Image(Bitmap photo, Date date, Date time, Location location) {
        this.fav = false;
        this.photo = photo;
        this.date = date;
        this.time = time;
        this.location = location;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean f) {
        fav = f;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void removeTag(String tag) {
        if (this.hasTag(tag)) {
            tags.remove(tag);
        }
    }

    public String getTagAt(Integer pos) {
        return tags.get(pos);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
}
