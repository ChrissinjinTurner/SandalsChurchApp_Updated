package com.android.christophersinjinturner.sandalschurchapp;

import java.io.Serializable;

/**
 * This is a object class for the Series, full of getters and setters for the different
 * characteristics of a Series.
 */
public class Series implements Serializable {
    private int id;
    private String title;
    private String desc;
    private String num_sermons;
    private String image_sd;
    private String image_hd;
    private String date;
    private String feed_url;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNum_sermons() {
        return num_sermons;
    }

    public void setNum_sermons(String num_sermons) {
        this.num_sermons = num_sermons;
    }

    public String getImage_sd() {
        return image_sd;
    }

    public void setImage_sd(String image_sd) {
        this.image_sd = image_sd;
    }

    public String getImage_hd() {
        return image_hd;
    }

    public void setImage_hd(String image_hd) {
        this.image_hd = image_hd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFeed_url() {
        return feed_url;
    }

    public void setFeed_url(String feed_url) {
        this.feed_url = feed_url;
    }
}