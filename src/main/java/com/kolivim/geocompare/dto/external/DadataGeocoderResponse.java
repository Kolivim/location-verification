package com.kolivim.geocompare.dto.external;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadataGeocoderResponse {

    private List<Suggestion> suggestions;

    @Data
    public static class Suggestion {

        private String value;

        @JsonProperty("data")
        private SuggestionData data;

    }


    @Data
    public static class SuggestionData {

        @JsonProperty("geo_lat")
        private String geoLat;

        @JsonProperty("geo_lon")
        private String geoLon;

    }

}