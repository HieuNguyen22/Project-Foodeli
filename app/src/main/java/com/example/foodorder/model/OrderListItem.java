package com.example.foodorder.model;

public class OrderListItem {
    String id, total_money, delivery_money, total_bill_money, time_order;

    public OrderListItem() {
    }

    public OrderListItem(String id, String total_money, String delivery_money, String total_bill_money, String time_order) {
        this.id = id;
        this.total_money = total_money;
        this.delivery_money = delivery_money;
        this.total_bill_money = total_bill_money;
        this.time_order = time_order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotal_money() {
        return total_money;
    }

    public void setTotal_money(String total_money) {
        this.total_money = total_money;
    }

    public String getDelivery_money() {
        return delivery_money;
    }

    public void setDelivery_money(String delivery_money) {
        this.delivery_money = delivery_money;
    }

    public String getTotal_bill_money() {
        return total_bill_money;
    }

    public void setTotal_bill_money(String total_bill_money) {
        this.total_bill_money = total_bill_money;
    }

    public String getTime_order() {
        return time_order;
    }

    public void setTime_order(String time_order) {
        this.time_order = time_order;
    }
}
