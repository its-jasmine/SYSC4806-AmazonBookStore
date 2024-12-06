package com.SYSC4806;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Review {
    private int rating;
    private String username;
    private String review;
    @Id
    @GeneratedValue
    private Long id;

    public Review(int rating, String username, String review) {
        this.rating = rating;
        this.username = username;
        this.review = review;
    }

    public Review() {

    }

    public int getRating() {
        return rating;
    }
    public String getUsername() {
        return username;
    }
    public String getReview() {
        return review;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
