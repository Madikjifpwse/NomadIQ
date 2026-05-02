package com.nomadiq.app.models;

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

    @SerializedName("is_visited")
    private boolean isVisited;

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    @SerializedName("popularity_score")
    private float popularityScore;

    @SerializedName("experience_level")
    private String experienceLevel;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public float getPopularityScore() { return popularityScore; }
    public String getExperienceLevel() { return experienceLevel; }
    public String getPlaceType() { return place_type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}