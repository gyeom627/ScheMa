package com.schema.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GovAddressApiService {

    // 키워드를 이용한 주소 검색
    @GET("addrLinkApi.do")
    Call<GovAddressResponse> searchAddress(
        @Query("confmKey") String apiKey,
        @Query("currentPage") int currentPage,
        @Query("countPerPage") int countPerPage,
        @Query("keyword") String keyword,
        @Query("resultType") String resultType
    );

    // 좌표를 이용한 주소 검색 (리버스 지오코딩)
    @GET("addrCoordApi.do")
    Call<GovAddressResponse> searchCoord(
        @Query("confmKey") String apiKey,
        @Query("x") String x, // 경도
        @Query("y") String y, // 위도
        @Query("coordType") String coordType, // WGS84
        @Query("outputType") String outputType // json
    );
}
