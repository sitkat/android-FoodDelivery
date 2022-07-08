package com.example.fooddelivery.Model;

public class Users {
    private String _name, _phone, _password, _image;
    private Float _points;

    public Users() {

    }

    public Users(String _name, String _phone, String _password, Float _points) {
        this._name = _name;
        this._phone = _phone;
        this._password = _password;
        this._points = _points;
    }

    public String get_image() {
        return _image;
    }

    public void set_image(String _image) {
        this._image = _image;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public Float get_points() {
        return _points;
    }

    public void set_points(Float _points) {
        this._points = _points;
    }
}
