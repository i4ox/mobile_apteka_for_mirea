package ru.artlkv.aptekaformirea.models;


import androidx.annotation.StringDef;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

public class Order {
    private String uniqueID;
    private String address;
    private String preURL;
    private Double price = 0.0;
    private Double shippingCharge = 0.0; // Плата за доставку
    private Long createdAt;
    private String orderID;
    private String status = Status.PENDING;
    private String note = "";
    private String orderPath;
    private String sellerNote = "";

    @StringDef({Status.PENDING, Status.ACKNOWLEDGED, Status.CONFIRMED, Status.CANCELED, Status.COMPLETED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {

        String PENDING = "PENDING";
        String ACKNOWLEDGED = "ACKNOWLEDGED";
        String CONFIRMED = "CONFIRMED";
        String CANCELED = "CANCELED";
        String COMPLETED = "COMPLETED";
    }

    public Order() {}

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPreURL() {
        return preURL;
    }

    public void setPreURL(String preURL) {
        this.preURL = preURL;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getShippingCharge() {
        return shippingCharge;
    }

    public void setShippingCharge(Double shippingCharge) {
        this.shippingCharge = shippingCharge;
    }

    public Map<String, String> getCreatedAt() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getCreatedAtLong() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getStatus() {
        return status;
    }

    @Status
    public void setStatus(@Status String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderPath() {
        return orderPath;
    }

    public void setOrderPath(String orderPath) {
        this.orderPath = orderPath;
    }

    public String getSellerNote() {
        return sellerNote;
    }

    public void setSellerNote(String sellerNote) {
        this.sellerNote = sellerNote;
    }
}
