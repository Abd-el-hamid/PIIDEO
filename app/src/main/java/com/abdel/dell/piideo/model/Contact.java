package com.abdel.dell.piideo.model;

/**
 * Created by DELL on 14/05/2017.
 */

public class Contact {

    private String phone;
    private String name;
    private boolean exist;

    public Contact() {
        exist = false;
    }

    public Contact(String phone, String name, boolean exist) {
        this.phone = phone;
        this.name = name;
        this.exist = exist;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", exist=" + exist +
                '}';
    }
}
