package com.example.iamatourist;

import android.graphics.Bitmap;
import android.net.Uri;

public class Image {
    private String title;
    private String desc;
    private Bitmap photo;
    private String[] tags;
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

    public Image(String title, String desc, Bitmap photo, String[] tags) {
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

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
