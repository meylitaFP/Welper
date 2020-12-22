package com.example.welper.base;

public class House {
    String houseId;
    String name;
    String model;
    int size;
    int level;
    int numOfRoom;
    String picName;
    String date;

    public House() {
    }

    public House(String name, String model, int size, int level, int numOfRoom, String picName, String date) {
        this.name = name;
        this.model = model;
        this.size = size;
        this.level = level;
        this.numOfRoom = numOfRoom;
        this.picName = picName;
        this.date = date;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getHouseId() {
        return houseId;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public int getSize() {
        return size;
    }

    public int getLevel() {
        return level;
    }

    public int getNumOfRoom() {
        return numOfRoom;
    }

    public String getPicName() {
        return picName;
    }

    public String getDate() {
        return date;
    }
}
