package com.schema.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NaverReverseGeocodingResponse {

    @SerializedName("results")
    public List<Result> results;

    public static class Result {
        @SerializedName("region")
        public Region region;

        @SerializedName("land")
        public Land land;

        public String getFullAddress() {
            if (region == null || land == null) return "주소를 찾을 수 없습니다.";
            StringBuilder address = new StringBuilder();
            if (region.area1 != null) address.append(region.area1.name).append(" ");
            if (region.area2 != null) address.append(region.area2.name).append(" ");
            if (land.name != null) address.append(land.name).append(" ");
            if (land.number1 != null) address.append(land.number1);
            return address.toString().trim();
        }
    }

    public static class Region {
        @SerializedName("area1")
        public Area area1; // 시/도
        @SerializedName("area2")
        public Area area2; // 시/군/구
    }

    public static class Area {
        @SerializedName("name")
        public String name;
    }

    public static class Land {
        @SerializedName("name")
        public String name; // 도로명 또는 동/리
        @SerializedName("number1")
        public String number1; // 건물 번호
    }
}
