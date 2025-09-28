package com.schema.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VWorldApiService {

    // 주소 검색 (순방향 지오코딩)
    @GET("req/address")
    Call<VWorldAddressSearchResponse> searchAddress(
            @Query("service") String service,
            @Query("request") String request,
            @Query("query") String query,
            @Query("key") String key,
            @Query("type") String type,
            @Query("format") String format
    );

    // 역지오코딩 (좌표 -> 주소)
    @GET("req/address")
    Call<VWorldReverseGeocodeResponse> reverseGeocode(
            @Query("service") String service,
            @Query("request") String request,
            @Query("point") String point,
            @Query("key") String key,
            @Query("type") String type,
            @Query("format") String format
    );
}