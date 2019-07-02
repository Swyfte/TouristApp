package com.example.iamatourist;

import android.graphics.Bitmap;

public class image {
    private String title;
    private String desc;
    private Bitmap photo;
    private String[] tags;

    public image(String title, String desc, Bitmap photo, String[] tags) {
        this.title = title;
        this.desc = desc;
        this.photo = photo;
        this.tags = tags;
    }

    public image() {
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
