package com.example.welper.base;

public class Logo {
    String logoId;
    String name;
    String color;
    String model;
    String type;
    String picName;
    String date;

    public Logo() {
    }

    public Logo(String name, String color, String model, String type, String picName, String date) {
        this.name = name;
        this.color = color;
        this.model = model;
        this.type = type;
        this.picName = picName;
        this.date = date;
    }

    public String getLogoId() {
        return logoId;
    }

    public void setLogoId(String logoId) {
        this.logoId = logoId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getModel() {
        return model;
    }

    public String getType() {
        return type;
    }

    public String getPicName() {
        return picName;
    }

    public String getDate() {
        return date;
    }
}
