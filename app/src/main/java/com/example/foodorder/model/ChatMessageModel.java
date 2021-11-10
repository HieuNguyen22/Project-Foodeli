package com.example.foodorder.model;

public class ChatMessageModel {
    String from, text, time;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String from, String text, String time) {
        this.from = from;
        this.text = text;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
