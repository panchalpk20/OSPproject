package com.anngrynerds.ospproject.pojo;

public class QuantityDetails {
    private String item_name;
    private String quatity;

    public QuantityDetails(String itemname, String Quantity){
        this.item_name=itemname;
        this.quatity=Quantity;
    }
    public String getItem_name() {
        return item_name;
    }

    public String setItem_name(String item_name) {
        this.item_name = item_name;
        return item_name;
    }
    public String getQuatity() {
        return quatity;
    }

    public String setQuatity(String quatity) {
        this.quatity = quatity;
        return quatity;
    }

}
