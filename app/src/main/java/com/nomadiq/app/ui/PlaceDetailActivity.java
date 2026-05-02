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

        String name = getIntent().getStringExtra("place_name");
        String desc = getIntent().getStringExtra("place_description");
        String addr = getIntent().getStringExtra("place_address");
        double rating = getIntent().getDoubleExtra("place_rating", 0.0);

        ((TextView) findViewById(R.id.detailName)).setText(name);
        ((TextView) findViewById(R.id.detailDescription)).setText(desc);
        ((TextView) findViewById(R.id.detailAddress)).setText(addr != null ? addr : "Address unavailable");
        ((TextView) findViewById(R.id.detailRating)).setText(String.valueOf(rating));

        Button btnVisited = findViewById(R.id.btnMarkVisited);
        btnVisited.setOnClickListener(v -> markVisited(btnVisited));
    }

    private void markVisited(Button btn) {
        if (placeId == null || placeId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid Place ID", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.markAsVisited(new VisitedRequest(placeId)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PlaceDetailActivity.this, "Added to visited!", Toast.LENGTH_SHORT).show();
                    btn.setText("Visited");
                    btn.setEnabled(false);
                    btn.setBackgroundColor(Color.GRAY);
                } else if (response.code() == 403) {
                    Toast.makeText(PlaceDetailActivity.this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PlaceDetailActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PlaceDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}