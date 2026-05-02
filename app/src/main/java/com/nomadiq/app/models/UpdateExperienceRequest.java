package com.nomadiq.app.models;

import com.google.gson.annotations.SerializedName;

public class UpdateExperienceRequest {
    @SerializedName("experience_level")
    private final String experienceLevel;

    public UpdateExperienceRequest(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
}
