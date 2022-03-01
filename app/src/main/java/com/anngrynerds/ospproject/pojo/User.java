package com.anngrynerds.ospproject.pojo;


public class User {
    String mobNo;
    String address;
    String name;
    String lat;
    String lang;
    String role;
    String city;

    public User(){}

    public User(String mobNo, String address, String name, String lat, String lang, String role, String city) {
        this.mobNo = mobNo;
        this.address = address;
        this.name = name;
        this.lat = lat;
        this.lang = lang;
        this.role = role;
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMobNo() {
        return mobNo;
    }

    public void setMobNo(String mobNo) {
        this.mobNo = mobNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

