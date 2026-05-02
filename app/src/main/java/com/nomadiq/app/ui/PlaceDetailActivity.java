package com.nomadiq.app.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nomadiq.app.R;
import com.nomadiq.app.models.VisitedRequest;
import com.nomadiq.app.network.ApiClient;
import com.nomadiq.app.network.ApiService;

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

        placeId = getIntent().getStringExtra("place_id");
        String placeName = getIntent().getStringExtra("place_name");
        String placeDescription = getIntent().getStringExtra("place_description");
        String placeAddress = getIntent().getStringExtra("place_address");
        double placeRating = getIntent().getDoubleExtra("place_rating", 0.0);

        isCurrentlyVisited = getIntent().getBooleanExtra("place_is_visited", false);

        ((TextView) findViewById(R.id.detailName)).setText(placeName);
        ((TextView) findViewById(R.id.detailDescription)).setText(placeDescription);
        ((TextView) findViewById(R.id.detailAddress)).setText(placeAddress != null ? placeAddress : "Address unavailable");
        ((TextView) findViewById(R.id.detailRating)).setText(String.valueOf(placeRating));

        btnVisited = findViewById(R.id.btnMarkVisited);

        updateButtonUI();

        btnVisited.setOnClickListener(v -> {
            if (isCurrentlyVisited) {
                removeFromVisited(placeId);
            } else {
                markVisited(placeId);
            }
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
        btnVisited.setEnabled(true);
    }

    private void markVisited(String placeId) {
        if (placeId == null || placeId.isEmpty()) return;

        apiService.markAsVisited(new VisitedRequest(placeId)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() || response.code() == 400) {
                    isCurrentlyVisited = true;
                    updateButtonUI();
                    Toast.makeText(PlaceDetailActivity.this, "Added to visited!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PlaceDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromVisited(String placeId) {
        apiService.removeFromVisited(placeId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isCurrentlyVisited = false;
                    updateButtonUI();
                    setResult(RESULT_OK);
                    Toast.makeText(PlaceDetailActivity.this, "Removed from visited", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlaceDetailActivity.this, "Error removing: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PlaceDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}