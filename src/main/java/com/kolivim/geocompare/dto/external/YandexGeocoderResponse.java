package com.kolivim.geocompare.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class YandexGeocoderResponse {

    @JsonProperty("response")
    private Response response;

    @Data
    public static class Response {
        @JsonProperty("GeoObjectCollection")
        private GeoObjectCollection geoObjectCollection;
    }

    @Data
    public static class GeoObjectCollection {
        @JsonProperty("featureMember")
        private List<FeatureMember> featureMember;
    }

    @Data
    public static class FeatureMember {
        @JsonProperty("GeoObject")
        private GeoObject geoObject;
    }

    @Data
    public static class GeoObject {
        @JsonProperty("Point")
        private Point point;
    }

    @Data
    public static class Point {
        @JsonProperty("pos")
        private String pos;
    }

}