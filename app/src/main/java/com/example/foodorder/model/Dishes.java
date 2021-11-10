package com.example.foodorder.model;

public class Dishes {
    private String id;
    private String name;
    private String image;
    private String price;
    private boolean isLiked;

    public Dishes() {
    }

    public Dishes(String id, String name, String image, String price, boolean isLiked) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.isLiked = isLiked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
