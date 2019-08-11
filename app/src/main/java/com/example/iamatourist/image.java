package com.example.iamatourist;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

public class Image {
    private String title;
    private String desc;
    private Bitmap photo;
    private ArrayList<String> tags = new ArrayList<>();
    private Uri fileLoc;
    private boolean fav;

    public Image() {
        this.fav = false;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean f) {
        fav = f;
    }

    public Image(String title, String desc, Bitmap photo, ArrayList<String> tags) {
        this.title = title;
        this.desc = desc;
        this.photo = photo;
        this.tags = tags;
    }

    public Uri getFileLoc() {
        return fileLoc;
    }

    public void setFileLoc(Uri fileLoc) {
        this.fileLoc = fileLoc;
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
