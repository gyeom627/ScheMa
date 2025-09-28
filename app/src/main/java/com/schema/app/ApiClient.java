package com.schema.app;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 클라이언트 인스턴스를 생성하고 관리하는 클래스입니다.
 * Geocoding, Directions 등 다양한 API 서비스를 위한 클라이언트를 중앙에서 관리합니다.
 */
public class ApiClient {

    // 네이버 Directions API의 기본 URL
    public static final String BASE_URL_DIRECTIONS = "https://naveropenapi.apigw.ntruss.com/map-direction/";
    // OpenWeatherMap API의 기본 URL
    public static final String BASE_URL_WEATHER = "https://api.openweathermap.org/data/2.5/";
    // 도로명주소 API의 기본 URL
    public static final String BASE_URL_GOV_ADDRESS = "https://business.juso.go.kr/addrlink/";

    /**
     * 네이버 API와 같이 인증 헤더가 필요한 클라이언트를 생성합니다.
     */
    private static Retrofit getNaverClient(String baseUrl, String clientId, String clientSecret) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor authInterceptor = chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("X-NCP-APIGW-API-KEY-ID", clientId)
                    .header("X-NCP-APIGW-API-KEY", clientSecret);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    /**
     * 인증 헤더가 필요 없는 범용 클라이언트를 생성합니다.
     */
    private static Retrofit getGenericClient(String baseUrl) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    /**
     * 도로명주소 API 서비스를 위한 Retrofit 서비스 인스턴스를 반환합니다.
     */
    public static GovAddressApiService getGovAddressApiService() {
        return getGenericClient(BASE_URL_GOV_ADDRESS).create(GovAddressApiService.class);
    }

    /**
     * Naver Directions API 서비스를 위한 Retrofit 서비스 인스턴스를 반환합니다.
     */
    public static NaverDirectionsApiService getDirectionsApiService(String clientId, String clientSecret) {
        return getNaverClient(BASE_URL_DIRECTIONS, clientId, clientSecret).create(NaverDirectionsApiService.class);
    }

    /**
     * OpenWeatherMap API 서비스를 위한 Retrofit 서비스 인스턴스를 반환합니다.
     */
    public static OpenWeatherMapApiService getWeatherApiService() {
        return getGenericClient(BASE_URL_WEATHER).create(OpenWeatherMapApiService.class);
    }
}
