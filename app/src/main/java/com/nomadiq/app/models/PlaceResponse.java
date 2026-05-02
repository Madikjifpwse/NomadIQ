package com.nomadiq.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceResponse {
    @SerializedName("places")
    private List<Place> places;
    public List<Place> getPlaces() { return places; }
}
