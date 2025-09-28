package com.schema.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("list")
    public List<Forecast> list;

    public static class Forecast {
        @SerializedName("main")
        public Main main;

        @SerializedName("weather")
        public List<Weather> weather;

        @SerializedName("dt_txt")
        public String dt_txt; // 예보 시간 (e.g., "2025-09-27 18:00:00")
    }

    public static class Main {
        @SerializedName("temp")
        public double temp;
    }

    public static class Weather {
        @SerializedName("main")
        public String main; // e.g., "Clear", "Clouds", "Rain"

        @SerializedName("description")
        public String description; // e.g., "맑음", "구름 조금"

        @SerializedName("icon")
        public String icon; // e.g., "01d"
    }
}
