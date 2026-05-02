package com.nomadiq.app.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Place {
    private String id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String category;
    private String place_type;
    private String address;

    @SerializedName("rating")
    private double rating;

    public String getAddress() {
        return address;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @SerializedName("popularity_score")
    private float popularityScore;

    @SerializedName("experience_level")
    private String experienceLevel;

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }
    public String getCategory() { return category; }

    public double getLongitude() {
        return longitude;
    }

    public float getPopularityScore() {
        return popularityScore;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public String getPlaceType() {
        return place_type;
    }

    public String getDescription() {
        return description;
    }

}
