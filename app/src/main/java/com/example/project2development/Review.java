package com.example.project2development;

import java.util.ArrayList;

public class Review {

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    public String getUser_email() {
        return user_email;
    }

    private String title;
    private String description;
    private String attraction_id;
    private int rating;
    private String user_email;

    public Review(String title, String description, String attraction_id, int rating, String user_email) {
        this.title = title;
        this.description = description;
        this.attraction_id = attraction_id;
        this.rating = rating;
        this.user_email = user_email;
    }
}
