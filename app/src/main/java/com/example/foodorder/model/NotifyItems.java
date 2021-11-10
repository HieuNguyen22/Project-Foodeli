package com.example.foodorder.model;

public class NotifyItems {
    String id, time_order, time_cancel;

    public NotifyItems() {
    }

    public NotifyItems(String id, String time_order, String time_cancel) {
        this.id = id;
        this.time_order = time_order;
        this.time_cancel = time_cancel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime_order() {
        return time_order;
    }

    public void setTime_order(String time_order) {
        this.time_order = time_order;
    }

    public String getTime_cancel() {
        return time_cancel;
    }

    public void setTime_cancel(String time_cancel) {
        this.time_cancel = time_cancel;
    }
}
