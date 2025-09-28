package com.schema.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NaverDirectionsResponse {

    @SerializedName("route")
    private Route route;

    public Route getRoute() {
        return route;
    }

    public static class Route {
        @SerializedName("trafast")
        private List<Path> trafast;

        public List<Path> getTrafast() {
            return trafast;
        }
    }

    public static class Path {
        public List<Double>[] path;
        @SerializedName("summary")
        private Summary summary;

        public Summary getSummary() {
            return summary;
        }

        public static class Summary {
            @SerializedName("duration")
            private long duration; // Duration in milliseconds

            public long getDuration() {
                return duration;
            }
        }
    }
}
