package com.schema.app;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VWorldReverseGeocodeResponse {
    @SerializedName("response")
    public Response response;

    public static class Response {
        @SerializedName("status")
        public String status;
        @SerializedName("result")
        public Result result;
    }

    public static class Result {
        @SerializedName("featureCollection")
        public FeatureCollection featureCollection;
    }

    public static class FeatureCollection {
        @SerializedName("features")
        public List<Feature> features;
    }

    public static class Feature {
        @SerializedName("properties")
        public Properties properties;
    }

    public static class Properties {
        @SerializedName("full_addr")
        public String full_addr;
        @SerializedName("addrdetail")
        public String addrdetail;
        @SerializedName("sido")
        public String sido;
        @SerializedName("sigungu")
        public String sigungu;
        @SerializedName("dong")
        public String dong;
        @SerializedName("ri")
        public String ri;
        @SerializedName("road_name")
        public String road_name;
        @SerializedName("building_no")
        public String building_no;
        @SerializedName("zipcode")
        public String zipcode;
    }
}