package com.nomadiq.app.models;

import com.google.gson.annotations.SerializedName;

public class UserRegisterRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("experience_level")
    private String experienceLevel; // "first_timer" или "advanced"

    public UserRegisterRequest(String username, String email, String password, String experienceLevel) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.experienceLevel = experienceLevel;
    }
}
