package com.example.iamatourist;

import android.graphics.Bitmap;
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
    private String location;


    /**
     * A default constructor, that initialises the image to not be a favourite
     */
    public Image() {
        this.fav = false;
    }

    /**
     * A constructor that builds an image based on the second set of information
     * @param title The image's title, as entered by the user
     * @param desc The image's description, as entered by the user
     * @param photo The image captured from the camera
     * @param tags The series of tags. The list is extracted by extractTags()
     */
    public Image(String title, String desc, Bitmap photo, ArrayList<String> tags) {
        this.fav = false;
        this.title = title;
        this.desc = desc;
        this.photo = photo;
        this.tags = tags;
    }

    /**
     * The constructor currently in use
     * @param photo The image captured from the camera
     * @param date The date, as extracted from a DatePickerDialog
     * @param time The time, as extracted from a TimePickerDialog
     * @param location The location, currently a string due to simplification
     */
    public Image(Bitmap photo, Date date, Date time, String location) {
        this.fav = false;
        this.photo = photo;
        this.date = date;
        this.time = time;
        this.location = location;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
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

    /**
     * Used to add a tag onto the list of tags. Note: Not sorted in any way
     * @param tag The tag to add
     */
    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Used to remove specific tags from the list of tags
     * @param tag The tag string to remove
     */
    public void removeTag(String tag) {
        if (this.hasTag(tag)) {
            tags.remove(tag);
        }
    }

    /**
     * Used to find tags at a specific position in the list.
     * Implemented because it may be useful at some point in development
     * @param pos the position in the arraylist
     * @return returns the tag, provided it's not out of bounds
     */
    public String getTagAt(Integer pos) {
        if (!(pos > tags.size())) {
            return tags.get(pos);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * This method checks if the tag is part of the list of tags.
     * This is to allow the image to be searched for by a tag instead of by name
     * @param tag The tag string to look for in the ArrayList of tags
     * @return A boolean response is all that is needed for this case
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    /**
     * This method takes the series of tags, separated by commas, as a single string. It then
     * separates the values by the commas, or the end of the file.
     * @param tagsList A string of comma separated values
     * @return The arraylist of string tags. Hopefully correctly separated.
     */
    ArrayList<String> extractTags(String tagsList) {
        int i = 0;
        ArrayList<String> tags = new ArrayList<>();
        while (i < tagsList.length()) {
            String tag = "";
            char c = tagsList.charAt(i);
            if (!(c == ',')) {
                String letter = String.valueOf(c);
                tag = tag + letter;
                i++;
            } else if (i == tagsList.length()-1) {
                tags.add(tag);
            } else {
                tags.add(tag);
                i += 2;
            }
        }
        if (tags.size() > 0) {
            return tags;
        } else {
            return null;
        }
    }
}
