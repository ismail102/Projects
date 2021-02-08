package com.bestapk.petukvai.model;

public class BandInfo {
    String imageUrl,name;

    public BandInfo(String name, String imageUrl) {
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }
}
