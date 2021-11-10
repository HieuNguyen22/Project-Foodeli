package com.example.foodorder.model;

public class Categories {
    String name_category;
    String amount;
    int image;
    int color;

    public Categories() {
    }

    public Categories(String name_category, String amount, int image, int color) {
        this.name_category = name_category;
        this.amount = amount;
        this.image = image;
        this.color = color;
    }

    public String getName_category() {
        return name_category;
    }

    public void setName_category(String name_category) {
        this.name_category = name_category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
