package com.nomadiq.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlaceListResponse {
    @SerializedName("places")
    private List<Place> places;

    @SerializedName("visited_places")
    private List<VisitedPlaceItem> visitedPlaces;

    public List<Place> getPlaces() {
        return places;
    }

    public List<VisitedPlaceItem> getVisitedItems() {
        return visitedPlaces;
    }
}