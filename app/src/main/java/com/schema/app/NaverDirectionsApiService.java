package com.schema.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NaverDirectionsApiService {
    @GET("v1/driving")
    Call<NaverDirectionsResponse> getDrivingRoute(
        @Query("start") String start, // "longitude,latitude"
        @Query("goal") String goal    // "longitude,latitude"
    );
}
