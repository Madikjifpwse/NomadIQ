package com.nomadiq.app.adapters;

import com.nomadiq.app.models.Place;
import java.util.List;

public class VisitedPlacesAdapter extends PlacesAdapter {
    public VisitedPlacesAdapter(List<Place> places, OnPlaceClickListener listener) {
        super(places, listener);
    }

}