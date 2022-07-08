package com.example.fooddelivery.Model;

public class History {
    String date, address;
    Long sum;

    public void setDate(String date) {
        this.date = date;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }
}
