package com.schema.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NaverReverseGeocodingApiService {
    @GET("v2/gc")
    Call<NaverReverseGeocodingResponse> reverseGeocode(
        @Header("X-NCP-APIGW-API-KEY-ID") String clientId,
        @Header("X-NCP-APIGW-API-KEY") String clientSecret,
        @Query("coords") String coords,
        @Query("orders") String orders,
        @Query("output") String output
    );
}
