package com.schema.app;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VWorldAddressSearchResponse {
    @SerializedName("response")
    public Response response;

    public static class Response {
        @SerializedName("status")
        public String status;
        @SerializedName("result")
        public Result result;
    }

    public static class Result {
        @SerializedName("items")
        public List<Item> items;
    }

    public static class Item {
        @SerializedName("address")
        public Address address;
        @SerializedName("point")
        public Point point;
    }

    public static class Address {
        @SerializedName("road")
        public String road;
        @SerializedName("parcel")
        public String parcel;
    }

    public static class Point {
        @SerializedName("x")
        public String x;
        @SerializedName("y")
        public String y;
    }
}