package com.anngrynerds.ospproject.pojo;

import java.util.ArrayList;

public class Order {
    ArrayList <OrderItem> list;

    String totalItems;
    String totalCost;
    String orderId;
    ArrayList<String> fromPost_id;

    public Order(ArrayList<OrderItem> list, String totalItems, String totalCost, String orderId, ArrayList<String> fromPost_id) {
        this.list = list;
        this.totalItems = totalItems;
        this.totalCost = totalCost;
        this.orderId = orderId;
        this.fromPost_id = fromPost_id;
    }

    public ArrayList<OrderItem> getList() {
        return list;
    }

    public void setList(ArrayList<OrderItem> list) {
        this.list = list;
    }

    public String getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ArrayList<String> getFromPost_id() {
        return fromPost_id;
    }

    public void setFromPost_id(ArrayList<String> fromPost_id) {
        this.fromPost_id = fromPost_id;
    }
}
