package com.nomadiq.app.network;

import com.nomadiq.app.models.Place;
import com.nomadiq.app.models.PlaceListResponse;
import com.nomadiq.app.models.PlaceResponse;
import com.nomadiq.app.models.TokenResponse;
import com.nomadiq.app.models.UserLoginRequest;
import com.nomadiq.app.models.UserRegisterRequest;
import com.nomadiq.app.models.UserResponse;
import com.nomadiq.app.models.VisitedRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.http.*;

public interface ApiService {
    @POST("api/v1/auth/login")
    Call<TokenResponse> login(@Body UserLoginRequest request);

    @POST("api/v1/auth/register")
    Call<UserResponse> register(@Body UserRegisterRequest request);

    @GET("api/v1/places")
    Call<PlaceListResponse> getPlaces(@Query("experience_level") String level);

    @GET("api/v1/users/me/visited")
    Call<PlaceListResponse> getMyVisitedPlaces();

    @POST("api/v1/visited")
    Call<ResponseBody> markAsVisited(@Body VisitedRequest request);
}

