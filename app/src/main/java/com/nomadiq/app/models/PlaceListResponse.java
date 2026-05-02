package com.nomadiq.app.models;
import java.util.List;

public class PlaceListResponse {
    private List<Place> places;
    private int total;

    public List<Place> getPlaces() { return places; }
    public void setPlaces(List<Place> places) { this.places = places; }
}