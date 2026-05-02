package com.nomadiq.app.models;

import com.google.gson.annotations.SerializedName;

public class UserLoginRequest {
    @SerializedName("username") // Должно совпадать с полем в Python
    private String username;

    @SerializedName("password")
    private String password;

    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}