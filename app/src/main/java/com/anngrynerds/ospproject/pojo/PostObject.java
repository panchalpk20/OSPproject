package com.anngrynerds.ospproject.pojo;

import java.util.ArrayList;

public class PostObject {

    String item_name;
    String item_qty;
    String item_price;
    ArrayList<String> filePathList;
    String date;
    String mobno;
    String post_id;
    String lat;
    String lang;
    String distance;
    int reportCount;

    public PostObject(
            String str_item_name,
            String str_item_qty,
            String str_item_price,
            ArrayList<String> filePathList,
            String str_date,
            String str_mobno,
            String post_id,
            String lat,
            String lang,
            String distance,
            int reportCount
            ) {


         this.item_name = str_item_name;
         this.item_qty = str_item_qty;
         this.item_price = str_item_price;
         this.filePathList = filePathList;
         this.date = str_date;
         this.mobno = str_mobno;
         this.post_id = post_id;
         this.lat = lat;
         this.lang = lang;
         this.distance = distance;
         this.reportCount = reportCount;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public PostObject() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(String item_qty) {
        this.item_qty = item_qty;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }

    public ArrayList<String> getFilePathList() {
        return filePathList;
    }

    public void setFilePathList(ArrayList<String> filePathList) {
        this.filePathList = filePathList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMobno() {
        return mobno;
    }

    public void setMobno(String mobno) {
        this.mobno = mobno;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    @Override
    public String toString() {
        return "PostObject{" +
                "item_name='" + item_name + '\'' +
                ", item_qty='" + item_qty + '\'' +
                ", item_price='" + item_price + '\'' +
                ", filePathList=" + filePathList +
                ", date='" + date + '\'' +
                ", mobno='" + mobno + '\'' +
                ", post_id='" + post_id + '\'' +
                '}';
    }
}
