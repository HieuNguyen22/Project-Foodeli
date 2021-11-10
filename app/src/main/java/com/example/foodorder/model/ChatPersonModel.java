package com.example.foodorder.model;

public class ChatPersonModel {
    String avatar, name, username;

    public ChatPersonModel() {
    }

    public ChatPersonModel(String avatar, String name, String username) {
        this.avatar = avatar;
        this.name = name;
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
