package com.nomadiq.app.adapters;

import com.nomadiq.app.models.Place;
import java.util.ArrayList;
import java.util.List;

public class VisitedPlacesAdapter extends PlacesAdapter {
    public VisitedPlacesAdapter(List<Place> places, OnPlaceClickListener listener) {
        super(places, listener);
    }

    @Override
    public void setPlaces(List<Place> newPlaces) {
        super.setPlaces(newPlaces != null ? newPlaces : new ArrayList<>());
    }
}