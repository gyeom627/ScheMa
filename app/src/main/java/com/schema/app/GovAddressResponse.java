package com.schema.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GovAddressResponse {

    @SerializedName("results")
    public Results results;

    public static class Results {
        @SerializedName("common")
        public Common common;

        @SerializedName("juso")
        public List<Juso> juso;
    }

    public static class Common {
        @SerializedName("errorMessage")
        public String errorMessage;

        @SerializedName("errorCode")
        public String errorCode;
    }

    public static class Juso {
        @SerializedName("roadAddr")
        public String roadAddr; // 전체 도로명 주소

        @SerializedName("jibunAddr")
        public String jibunAddr; // 지번 주소

        @SerializedName("entX")
        public String entX; // X 좌표

        @SerializedName("entY")
        public String entY; // Y 좌표
    }
}
