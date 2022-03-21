package com.anngrynerds.ospproject.pojo;

public class OrderItem {

    String name;
    String qty;
    String cost;
    String postId;

    public OrderItem() {
    }

    public OrderItem(String name, String qty, String cost, String postId) {
        this.name = name;
        this.qty = qty;
        this.cost = cost;
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    @Override
    public String toString() {
        return "OrderItem:" +
                "\nname='" + name + '\'' +
                ",\nqty='" + qty + '\'' +
                ", \ncost='" + cost + '\'' +
                ", \npostId='" + postId + '\'' +
                '}';
    }
}
