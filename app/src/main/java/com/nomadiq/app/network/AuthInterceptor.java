package com.nomadiq.app.network;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context appContext;

    public AuthInterceptor(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        if (path.contains("login") || path.contains("register")) {
            return chain.proceed(originalRequest);
        }

        SharedPreferences prefs = appContext.getSharedPreferences("NomadIQ_Prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");

        Request.Builder requestBuilder = originalRequest.newBuilder();

        if (token != null && !token.isEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }

        return chain.proceed(requestBuilder.build());
    }
}