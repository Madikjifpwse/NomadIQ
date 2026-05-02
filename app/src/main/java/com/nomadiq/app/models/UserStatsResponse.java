package com.nomadiq.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserStatsResponse {
    @SerializedName(value = "username", alternate = {"name"})
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName(value = "experience_level", alternate = {"experienceMode"})
    private String experienceLevel;

    @SerializedName("total_visited")
    private int totalVisited;

    @SerializedName("average_rating")
    private double averageRating;

    @SerializedName("most_common_category")
    private String mostCommonCategory;

    @SerializedName("most_common_tags")
    private List<String> mostCommonTags;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public List<String> getMostCommonTags() {
        return mostCommonTags;
    }

    public int getTotalVisited() {
        return totalVisited;
    }

    public double getAverageRating() {
        return averageRating;
    }
    public String getMostCommonCategory() {
        return mostCommonCategory;
    }
}
