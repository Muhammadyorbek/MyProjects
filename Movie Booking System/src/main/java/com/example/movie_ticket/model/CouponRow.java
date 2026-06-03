package com.example.movie_ticket.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CouponRow {
    public final StringProperty coupon_code;
    public final StringProperty discount_percentage;
    public final StringProperty status;

    public CouponRow(String coupon_code, String discountPercentage, String status) {
        this.coupon_code = new SimpleStringProperty(coupon_code);
        this.discount_percentage = new SimpleStringProperty(discountPercentage);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty coupon_codeProperty(){ return coupon_code; }
    public StringProperty discount_percentageProperty(){ return discount_percentage; }
    public StringProperty statusProperty(){ return status; }

    public boolean isActive() { return "1".equals(status.get()); }
    public String getCode()   { return coupon_code.get(); }
}