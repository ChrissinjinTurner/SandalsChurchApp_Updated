package com.android.christophersinjinturner.sandalschurchapp;

import java.io.Serializable;

/**
 * This is a object class for the Sermon, full of getters and setters for the different
 * characteristics of a Sermon.
 */
public class Sermon implements Serializable {
    private int id;
    private String title;
    private String desc;
    private String date;
    private int length;
    private String image_sd;
    private String image_hd;
    private String mp4_sd;
    private String mp4_hd;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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

    public String getMp4_sd() {
        return mp4_sd;
    }

    public void setMp4_sd(String mp4_sd) {
        this.mp4_sd = mp4_sd;
    }

    public String getMp4_hd() {
        return mp4_hd;
    }

    public void setMp4_hd(String mp4_hd) {
        this.mp4_hd = mp4_hd;
    }
}