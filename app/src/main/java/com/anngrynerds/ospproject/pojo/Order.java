package com.anngrynerds.ospproject.pojo;

public class Order {

    String orderId;
    String fromId;
    String toId;

    String name;
    String qty;
    String cost;
    String postId;

    String orderStatus;

    public Order() {
    }

//    public Order(ArrayList<OrderItem> list, String totalItems, String totalCost, String orderId, String fromId, String toId) {
//        this.list = list;
//        this.totalItems = totalItems;
//        this.totalCost = totalCost;
//        this.orderId = orderId;
//        this.fromId = fromId;
//        this.toId = toId;
//    }


    public Order(String orderId,
                 String fromId,
                 String toId,
                 String name, String qty, String cost, String postId,
                 String orderStatus
                 ) {
        this.orderId = orderId;
        this.fromId = fromId;
        this.toId = toId;
        this.name = name;
        this.qty = qty;
        this.cost = cost;
        this.postId = postId;
        this.orderStatus = orderStatus;
    }


    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", name='" + name + '\'' +
                ", qty='" + qty + '\'' +
                ", cost='" + cost + '\'' +
                ", postId='" + postId + '\'' +
                '}';
    }
}
