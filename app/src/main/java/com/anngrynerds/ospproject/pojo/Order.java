package com.anngrynerds.ospproject.pojo;

import java.util.ArrayList;

public class Order {
    ArrayList <OrderItem> list;

    String totalItems;
    String totalCost;
    String orderId;
    String fromId;
    String toId;

    public Order(ArrayList<OrderItem> list, String totalItems, String totalCost, String orderId, String fromId, String toId) {
        this.list = list;
        this.totalItems = totalItems;
        this.totalCost = totalCost;
        this.orderId = orderId;
        this.fromId = fromId;
        this.toId = toId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
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
}
