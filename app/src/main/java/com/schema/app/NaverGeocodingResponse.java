package com.schema.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NaverGeocodingResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("addresses")
    private List<Address> addresses;

    public String getStatus() {
        return status;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public static class Address {
        @SerializedName("roadAddress")
        private String roadAddress; // 도로명 주소

        @SerializedName("jibunAddress")
        private String jibunAddress; // 지번 주소

        @SerializedName("x")
        private String x; // Longitude

        @SerializedName("y")
        private String y; // Latitude

        public String getRoadAddress() {
            return roadAddress;
        }

        public String getJibunAddress() {
            return jibunAddress;
        }

        public String getX() {
            return x;
        }

        public String getY() {
            return y;
        }
    }
}
