package com.example.foodorder.model;

public class Places {
    int image;
    String name_place;
    String detail;
    String distance;
    String time;

    public Places(int image, String name_place, String detail, String distance, String time) {
        this.image = image;
        this.name_place = name_place;
        this.detail = detail;
        this.distance = distance;
        this.time = time;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName_place() {
        return name_place;
    }

    public void setName_place(String name_place) {
        this.name_place = name_place;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
