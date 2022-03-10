package com.anngrynerds.ospproject.pojo;

public class OrderItem {

    String name;
    String qty;
    String cost;

    public OrderItem() {
    }

    public OrderItem(String name, String qty, String cost) {
        this.name = name;
        this.qty = qty;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
