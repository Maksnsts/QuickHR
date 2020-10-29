package com.example.quickhr.Cards;

//cards
public class Cards {
    private String userId, name, profileImageUrl, phone,lastName , email, country , city, skills, experience , pdf , switch1;

    public Cards(String userId, String name, String profileImageUrl, String phone, String lastName, String email, String country, String city, String skills, String experience, String pdf, String switch1) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.phone = phone;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.city = city;
        this.skills = skills;
        this.experience = experience;
        this.pdf = pdf;
        this.switch1 = switch1;
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
        return "Phone: " + phone;
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

    public String getEmail() {
        return "Email: " + email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return "Country: " +country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return "City: " + city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience ;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getSwitch1() {
        return switch1;
    }

    public void setSwitch1(String switch1) {
        this.switch1 = switch1;
    }
}
