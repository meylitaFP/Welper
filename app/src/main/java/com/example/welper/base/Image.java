package com.example.welper.base;

import java.util.ArrayList;

public class Image {
    String imageId;
    String name;
    ArrayList<String> location;
    String timestamp;
    String type;

    public Image() {
    }

    public Image(String imageId, String name, ArrayList<String> location, String timestamp, String type) {
        this.imageId = imageId;
        this.name = name;
        this.location = location;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Image(String name, String timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getLocation() {
        return location;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
