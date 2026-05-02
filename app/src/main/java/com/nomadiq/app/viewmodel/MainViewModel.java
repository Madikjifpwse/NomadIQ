package com.nomadiq.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nomadiq.app.models.Place;

import java.util.List;

public class MainViewModel extends ViewModel {
    private MutableLiveData<List<Place>> places = new MutableLiveData<>();
    private String currentLevel = "first_timer";

    public void changeLevel(boolean isAdvanced) {
        currentLevel = isAdvanced ? "advanced" : "first_timer";
        loadPlaces();
    }

    private void loadPlaces() {
    }

    public LiveData<List<Place>> getPlaces() {
        return places;
    }
}