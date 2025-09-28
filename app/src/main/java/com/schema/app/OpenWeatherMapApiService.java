package com.schema.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApiService {
    @GET("forecast")
    Call<WeatherResponse> getForecast(
        @Query("lat") double lat,
        @Query("lon") double lon,
        @Query("appid") String apiKey,
        @Query("units") String units,
        @Query("lang") String lang
    );
}
