package com.nomadiq.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nomadiq.app.R;
import com.nomadiq.app.adapters.CategoryAdapter;
import com.nomadiq.app.adapters.PlacesAdapter;
import com.nomadiq.app.adapters.VisitedPlacesAdapter;
import com.nomadiq.app.models.Place;
import com.nomadiq.app.models.PlaceListResponse;
import com.nomadiq.app.network.ApiClient;
import com.nomadiq.app.network.ApiService;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private MapView map;
    private IMapController mapController;
    private ApiService apiService;
    private View topPanel, profileContainer, zoomCard;
    private RecyclerView placesListRecycler;

    private List<Place> allLoadedPlaces = new ArrayList<>();
    private String currentMode = "first_timer";
    private String currentCategoryFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("NomadIQ_Prefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        topPanel = findViewById(R.id.topPanel);
        placesListRecycler = findViewById(R.id.placesListRecycler);
        profileContainer = findViewById(R.id.profileContainer);
        zoomCard = findViewById(R.id.zoomCard);

        placesListRecycler.setLayoutManager(new LinearLayoutManager(this));
        apiService = ApiClient.getClient(this).create(ApiService.class);

        setupMap();
        setupCategoryRecycler();
        setupToggleLogic();
        setupBottomNavigation();
        setupZoomButtons();

        loadPlaces(currentMode);
    }

    private void setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(new GeoPoint(43.2389, 76.8897));
    }

    private void setupBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Сбрасываем видимость
            map.setVisibility(View.GONE);
            topPanel.setVisibility(View.GONE);
            zoomCard.setVisibility(View.GONE);
            placesListRecycler.setVisibility(View.GONE);
            profileContainer.setVisibility(View.GONE);

            if (id == R.id.nav_home) {
                map.setVisibility(View.VISIBLE);
                topPanel.setVisibility(View.VISIBLE);
                zoomCard.setVisibility(View.VISIBLE);
                displayPlacesOnMap();
            } else if (id == R.id.nav_search) {
                placesListRecycler.setVisibility(View.VISIBLE);
                loadPlaces(currentMode);
            } else if (id == R.id.nav_favorites) {
                placesListRecycler.setVisibility(View.VISIBLE);
                loadVisitedPlaces(); // Загружаем "Locations"
            } else if (id == R.id.nav_profile) {
                profileContainer.setVisibility(View.VISIBLE);
            }
            return true;
        });
    }

    private void openPlaceDetail(Place place) {
        if (place == null) return;
        Intent intent = new Intent(MainActivity.this, PlaceDetailActivity.class);
        intent.putExtra("place_id", place.getId());
        intent.putExtra("place_name", place.getName());
        intent.putExtra("place_description", place.getDescription());
        intent.putExtra("place_rating", place.getRating());
        intent.putExtra("place_category", place.getCategory());
        intent.putExtra("place_address", place.getAddress());
        startActivity(intent);
    }

    private void loadPlaces(String mode) {
        apiService.getPlaces(mode).enqueue(new Callback<PlaceListResponse>() {
            @Override
            public void onResponse(Call<PlaceListResponse> call, Response<PlaceListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allLoadedPlaces = response.body().getPlaces();
                    if (map.getVisibility() == View.VISIBLE) displayPlacesOnMap();
                    else setupPlacesList(allLoadedPlaces);
                }
            }
            @Override public void onFailure(Call<PlaceListResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadVisitedPlaces() {
        apiService.getMyVisitedPlaces().enqueue(new Callback<PlaceListResponse>() {
            @Override
            public void onResponse(Call<PlaceListResponse> call, Response<PlaceListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Используем отдельный адаптер для посещенных мест
                    VisitedPlacesAdapter adapter = new VisitedPlacesAdapter(
                            response.body().getPlaces(),
                            place -> openPlaceDetail(place)
                    );
                    placesListRecycler.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<PlaceListResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "History load failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPlacesList(List<Place> list) {
        placesListRecycler.setAdapter(new PlacesAdapter(list, this::openPlaceDetail));
    }

    private void displayPlacesOnMap() {
        if (map == null) return;
        map.getOverlays().clear();
        String dbKey = getDbKeyForCategory(currentCategoryFilter);
        for (Place place : allLoadedPlaces) {
            if (currentCategoryFilter.equals("All") || place.getCategory().equalsIgnoreCase(dbKey)) {
                addMarkerToMap(place);
            }
        }
        map.invalidate();
    }

    private void addMarkerToMap(Place place) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(place.getLatitude(), place.getLongitude()));
        marker.setIcon(createCustomMarker(place));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setOnMarkerClickListener((m, mapView) -> {
            openPlaceDetail(place);
            return true;
        });
        map.getOverlays().add(marker);
    }

    private Drawable createCustomMarker(Place place) {
        int color;
        int iconResId;
        String cat = (place.getCategory() != null) ? place.getCategory().toLowerCase() : "";
        switch (cat) {
            case "must_see": color = Color.parseColor("#FF6B6B"); break;
            case "local_secret": color = Color.parseColor("#6BCB77"); break;
            case "student_friendly": color = Color.parseColor("#FFD93D"); break;
            default: color = Color.parseColor("#4A90E2"); break;
        }

        String type = (place.getPlaceType() != null) ? place.getPlaceType().toLowerCase() : "";
        if (type.contains("park")) iconResId = R.drawable.ic_park;
        else if (type.contains("cafe")) iconResId = R.drawable.ic_cafe;
        else iconResId = R.drawable.ic_location;

        Bitmap bitmap = Bitmap.createBitmap(110, 110, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(color);
        shape.setStroke(5, Color.WHITE);
        shape.setBounds(0, 0, 110, 110);
        shape.draw(canvas);

        Drawable icon = ContextCompat.getDrawable(this, iconResId);
        if (icon != null) {
            icon.setTint(Color.WHITE);
            icon.setBounds(25, 25, 85, 85);
            icon.draw(canvas);
        }
        return new BitmapDrawable(getResources(), bitmap);
    }

    private void setupToggleLogic() {
        TextView btnFirst = findViewById(R.id.btnFirstTimer);
        TextView btnAdv = findViewById(R.id.btnAdvanced);
        btnFirst.setOnClickListener(v -> {
            currentMode = "first_timer";
            updateToggleUI(btnFirst, btnAdv);
            loadPlaces(currentMode);
        });
        btnAdv.setOnClickListener(v -> {
            currentMode = "advanced";
            updateToggleUI(btnAdv, btnFirst);
            loadPlaces(currentMode);
        });
    }

    private void updateToggleUI(TextView active, TextView inactive) {
        active.setBackgroundResource(R.drawable.bg_toggle_active); active.setTextColor(Color.WHITE);
        inactive.setBackgroundResource(0); inactive.setTextColor(Color.GRAY);
    }

    private void setupCategoryRecycler() {
        RecyclerView cr = findViewById(R.id.categoryRecycler);
        cr.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cr.setAdapter(new CategoryAdapter(Arrays.asList("All", "Must-See", "Local Secrets", "Student Friendly"), cat -> {
            currentCategoryFilter = cat; displayPlacesOnMap();
        }));
    }

    private void setupZoomButtons() {
        findViewById(R.id.btnZoomIn).setOnClickListener(v -> mapController.zoomIn());
        findViewById(R.id.btnZoomOut).setOnClickListener(v -> mapController.zoomOut());
    }

    private String getDbKeyForCategory(String uiName) {
        if (uiName.equals("Must-See")) return "must_see";
        if (uiName.equals("Local Secrets")) return "local_secret";
        if (uiName.equals("Student Friendly")) return "student_friendly";
        return "all";
    }

    @Override public void onResume() { super.onResume(); if (map != null) map.onResume(); }
    @Override public void onPause() { super.onPause(); if (map != null) map.onPause(); }
}