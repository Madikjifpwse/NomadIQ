package com.nomadiq.app.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.nomadiq.app.R;
import com.nomadiq.app.models.VisitedRequest;
import com.nomadiq.app.network.ApiClient;
import com.nomadiq.app.network.ApiService;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceDetailActivity extends AppCompatActivity {

    private ApiService apiService;
    private String placeId;
    private Button btnVisited;
    private boolean isCurrentlyVisited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Toolbar toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Получаем данные
        placeId = getIntent().getStringExtra("place_id");
        String placeName = getIntent().getStringExtra("place_name");
        String placeDescription = getIntent().getStringExtra("place_description");
        String placeAddress = getIntent().getStringExtra("place_address");
        String imageUrl = getIntent().getStringExtra("place_image_url");
        double placeRating = getIntent().getDoubleExtra("place_rating", 0.0);
        double lat = getIntent().getDoubleExtra("place_latitude", 0.0);
        double lon = getIntent().getDoubleExtra("place_longitude", 0.0);
        isCurrentlyVisited = getIntent().getBooleanExtra("place_is_visited", false);

        ((TextView) findViewById(R.id.detailName)).setText(placeName);
        ((TextView) findViewById(R.id.detailDescription)).setText(placeDescription);
        ((TextView) findViewById(R.id.detailAddress)).setText(placeAddress != null ? placeAddress : "Address unavailable");
        ((TextView) findViewById(R.id.detailRating)).setText(String.valueOf(placeRating));

        ImageView detailImage = findViewById(R.id.detailImage);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_placeholder_mountain) // Показываем горы, пока качается основное фото
                    .error(R.drawable.ic_placeholder_mountain)       // Показываем горы, если ссылка битая
                    .into(detailImage);
        } else {
            detailImage.setImageResource(R.drawable.ic_placeholder_mountain);
        }

        Button btnGoogleMaps = findViewById(R.id.btnViewOnGoogleMaps);
        btnGoogleMaps.setOnClickListener(v -> {
            if (lat != 0.0 && lon != 0.0) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", lat, lon, lat, lon, placeName);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon)));
                }
            }
        });

        btnVisited = findViewById(R.id.btnMarkVisited);
        updateButtonUI();

        btnVisited.setOnClickListener(v -> {
            if (isCurrentlyVisited) removeFromVisited(placeId);
            else markVisited(placeId);
        });
    }

    private void updateButtonUI() {
        if (isCurrentlyVisited) {
            btnVisited.setText("Visited (Tap to remove)");
            btnVisited.setBackgroundColor(Color.GRAY);
        } else {
            btnVisited.setText("I've been here");
            btnVisited.setBackgroundColor(Color.parseColor("#6200EE"));
        }
    }

    private void markVisited(String placeId) {
        if (placeId == null) return;
        apiService.markAsVisited(new VisitedRequest(placeId)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    isCurrentlyVisited = true;
                    updateButtonUI();
                    Toast.makeText(PlaceDetailActivity.this, "Marked as visited!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    private void removeFromVisited(String placeId) {
        apiService.removeFromVisited(placeId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isCurrentlyVisited = false;
                    updateButtonUI();
                    Toast.makeText(PlaceDetailActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}