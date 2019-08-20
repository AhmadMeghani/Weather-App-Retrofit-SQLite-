package com.abcx.retrofit;

import com.google.gson.annotations.SerializedName;

public class Post {

    private String lat;
    private String lon;
    private String main;
    private String description;

   /* public Post(int lat, String title, String text) {
        this.userId = userId;
        this.title = title;
        this.text = text;
    }*/

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }
}
