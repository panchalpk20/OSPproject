package com.anngrynerds.ospproject.pojo;

import java.util.ArrayList;

public class Order {
    ArrayList <OrderItem> list;
    ArrayList <PostObject> list1;

    String name;
    String totalItems;
    String totalCost;
    String orderId;
    String fromId;
    String toId;
    ArrayList<String> filePathList;


    public Order() {
    }

    public Order(ArrayList<OrderItem> list, String totalItems, String totalCost, String orderId, String fromId, String toId) {
        this.list = list;
        this.totalItems = totalItems;
        this.totalCost = totalCost;
        this.orderId = orderId;
        this.fromId = fromId;
        this.toId = toId;
    }
    public Order(ArrayList<PostObject>postObjects,ArrayList<String> file){
        this.filePathList=file;
        this.list1=postObjects;

    }
    public ArrayList<String> getFilePathList() {
        return filePathList;
    }

    public void setFilePathList(ArrayList<String> filePathList) {
        this.filePathList = filePathList;
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

    @Override
    public String toString() {
        return "Order{" +
                "\nlist=" + list +
                ", \ntotalItems='" + totalItems + '\'' +
                ", \ntotalCost='" + totalCost + '\'' +
                ", \norderId='" + orderId + '\'' +
                ", \nfromId='" + fromId + '\'' +
                ", \ntoId='" + toId + '\'' +
                '}';
    }
}
