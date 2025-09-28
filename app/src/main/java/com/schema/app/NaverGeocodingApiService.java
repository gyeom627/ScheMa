package com.schema.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NaverGeocodingApiService {
    @GET("v2/geocode")
    Call<NaverGeocodingResponse> geocode(
        @Header("X-NCP-APIGW-API-KEY-ID") String clientId,
        @Header("X-NCP-APIGW-API-KEY") String clientSecret,
        @Query("query") String query
    );
}
