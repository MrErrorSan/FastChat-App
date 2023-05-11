package com.fastian.fastchat;

public class User {
    private String uid, name, phoneNumber, email, profileImg;

    public User(){

    }
    public User(String uid, String name, String phoneNumber,String email, String profileImg) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImg = profileImg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImg;
    }

    public void setProfileImage(String profileImg) {
        this.profileImg = profileImg;
    }
}
