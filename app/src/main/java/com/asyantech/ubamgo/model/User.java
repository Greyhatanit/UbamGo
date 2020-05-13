package com.asyantech.ubamgo.model;

public class User {
    public String user_id, user_img, user_name, user_address, user_phone, user_email;

    public User(){
        //Public No Arg Constructor Needed
    }

    public User(String user_id, String user_img,
                String user_name, String user_address, String user_phone, String user_email) {
        this.user_id = user_id;
        this.user_img = user_img;
        this.user_name = user_name;
        this.user_address = user_address;
        this.user_phone = user_phone;
        this.user_email = user_email;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
