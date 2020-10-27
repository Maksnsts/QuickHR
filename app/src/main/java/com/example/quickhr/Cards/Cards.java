package com.example.quickhr.Cards;

//cards
public class Cards {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String phone; // step 1
    private String lastName;

    public Cards(String userId, String name, String profileImageUrl, String phone) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.phone = phone;
    }

    public Cards(String userId, String name, String profileImageUrl, String phone, String lastName) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.phone = phone;
        this.lastName = lastName;

    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
