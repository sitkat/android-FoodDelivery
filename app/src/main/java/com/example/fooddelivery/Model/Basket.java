package com.example.fooddelivery.Model;

public class Basket {
    public String count, pid, pk, product_name, product_price, product_image;

    public Basket() {
    }

    public Basket(String count, String pid, String pk, String product_name, String product_price, String product_image) {
        this.count = count;
        this.pid = pid;
        this.pk = pk;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_image = product_image;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getCount() {
        return count;
    }


    public String getPid() {
        return pid;
    }


    public String getPk() {
        return pk;
    }


}
