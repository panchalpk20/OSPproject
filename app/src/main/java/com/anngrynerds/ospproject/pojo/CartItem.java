package com.anngrynerds.ospproject.pojo;

public class CartItem {
    String itemName;
    String qtyInKg;
    String costPerKg;
    String originalPostId;

    public CartItem(String itemName, String qtyInKg, String costPerKg, String originalPostId) {
        this.itemName = itemName;
        this.qtyInKg = qtyInKg;
        this.costPerKg = costPerKg;
        this.originalPostId = originalPostId;
    }

    public CartItem() {
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQtyInKg() {
        return qtyInKg;
    }

    public void setQtyInKg(String qtyInKg) {
        this.qtyInKg = qtyInKg;
    }

    public String getCostPerKg() {
        return costPerKg;
    }

    public void setCostPerKg(String costPerKg) {
        this.costPerKg = costPerKg;
    }

    public String getOriginalPostId() {
        return originalPostId;
    }

    public void setOriginalPostId(String originalPostId) {
        this.originalPostId = originalPostId;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "itemName='" + itemName + '\'' +
                ", qtyInKg='" + qtyInKg + '\'' +
                ", costPerKg='" + costPerKg + '\'' +
                ", originalPostId='" + originalPostId + '\'' +
                '}';
    }
}
