package com.example.musicmanagement;

import android.graphics.Bitmap;

public class Artist {
    private String macasi;
    private String tencasi;
    private int sinhnam;
    private Bitmap image;

    // Constructor
    public Artist(String macasi, String tencasi, int sinhnam, Bitmap image) {
        this.macasi = macasi;
        this.tencasi = tencasi;
        this.sinhnam = sinhnam;
        this.image = image;
    }

    // Getters
    public String getMacasi() {
        return macasi;
    }

    public String getTencasi() {
        return tencasi;
    }

    public int getSinhnam() {
        return sinhnam;
    }

    public Bitmap getImage() {
        return image;
    }

    // Setters (optional)
    public void setImage(Bitmap image) {
        this.image = image;
    }
}
