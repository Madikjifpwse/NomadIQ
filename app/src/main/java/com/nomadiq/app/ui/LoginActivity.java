package com.nomadiq.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.nomadiq.app.R;
import com.nomadiq.app.models.TokenResponse;
import com.nomadiq.app.models.UserLoginRequest;
import com.nomadiq.app.network.ApiClient;
import com.nomadiq.app.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String AUTH_PREFS = "auth_prefs";
    private static final String ACCESS_TOKEN_KEY = "access_token";

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasSavedToken()) {
            startMainScreen();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter your username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        UserLoginRequest request = new UserLoginRequest(username, password);

        apiService.login(request).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    saveToken(token);

                    Toast.makeText(LoginActivity.this, "The entrance is completed!", Toast.LENGTH_SHORT).show();

                    startMainScreen();
                } else {
                    Toast.makeText(LoginActivity.this, "Error: invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences preferences = getSharedPreferences(AUTH_PREFS, MODE_PRIVATE);
        preferences.edit().putString(ACCESS_TOKEN_KEY, token).apply();
    }

    private boolean hasSavedToken() {
        SharedPreferences preferences = getSharedPreferences(AUTH_PREFS, MODE_PRIVATE);
        String token = preferences.getString(ACCESS_TOKEN_KEY, null);
        return token != null && !token.isEmpty();
    }

    private void startMainScreen() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}