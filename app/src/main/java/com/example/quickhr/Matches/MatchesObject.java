package com.example.quickhr.Matches;

public class MatchesObject {
    private String userId;
    private String name;
    private String prodileImageUrl;

    public MatchesObject(String userId, String name, String prodileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.prodileImageUrl = prodileImageUrl;
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

    public String getProdileImageUrl() {
        return prodileImageUrl;
    }

    public void setProdileImageUrl(String prodileImageUrl) {
        this.prodileImageUrl = prodileImageUrl;
    }
}
