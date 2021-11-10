package com.example.foodorder.model;

public class CartItems {
    String name,price,image;
    int amount,total;

    public CartItems() {
    }

    public CartItems(String name, String price, String image, int amount, int total) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.amount = amount;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
