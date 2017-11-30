package com.huduan.contactstest.model;

/**
 * Created by huduan on 17-11-24.
 */

public class ContactItem {
    private int img;
    private int phone;
    private int message;
    private String contactName;
    private String phoneNumber;
    private String sortkey;

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getSortkey() {
        return sortkey;
    }

    public void setSortkey(String sortkey) {
        this.sortkey = sortkey;
    }
}
