package com.nomadiq.app.models;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("experience_level")
    private String experienceLevel;

    // Геттеры
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
