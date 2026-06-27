package com.kolivim.geocompare.service.geocoding;

import com.kolivim.geocompare.dto.external.DadataGeocoderResponse;
import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.exception.GeocodingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DadataResponseParserTest {

    private final DadataResponseParser parser = new DadataResponseParser();

    @Test
    @DisplayName("Проверка правильности парсинга координат")
    void parseCoordinatesReturnCoordinatesWhenValidResponse() {
        DadataGeocoderResponse response = new DadataGeocoderResponse();
        DadataGeocoderResponse.Suggestion suggestion = new DadataGeocoderResponse.Suggestion();
        DadataGeocoderResponse.SuggestionData data = new DadataGeocoderResponse.SuggestionData();
        data.setGeoLat("55.7539");
        data.setGeoLon("37.6208");
        suggestion.setData(data);
        response.setSuggestions(java.util.List.of(suggestion));

        CoordinatesDto coords = parser.parseCoordinates(response);
        assertEquals(55.7539, coords.getLat());
        assertEquals(37.6208, coords.getLon());
    }

    @Test
    @DisplayName("Проверка выброса исключения при переданном Null")
    void parseCoordinatesThrowExceptionWhenResponseIsNull() {
        assertThrows(GeocodingException.class, () -> parser.parseCoordinates(null));
    }

    @Test
    @DisplayName("Проверка выброса исключения GeocodingException, при отсутствии Suggestion")
    void parseCoordinatesThrowExceptionWhenNoSuggestions() {
        DadataGeocoderResponse response = new DadataGeocoderResponse();
        response.setSuggestions(java.util.List.of());
        assertThrows(GeocodingException.class, () -> parser.parseCoordinates(response));
    }

    @Test
    @DisplayName("Проверка выброса исключения GeocodingException, при отсутствии координат/ы")
    void parseCoordinatesThrowExceptionWhenCoordinatesAreMissing() {
        DadataGeocoderResponse response = new DadataGeocoderResponse();
        DadataGeocoderResponse.Suggestion suggestion = new DadataGeocoderResponse.Suggestion();
        suggestion.setData(new DadataGeocoderResponse.SuggestionData());
        response.setSuggestions(java.util.List.of(suggestion));
        assertThrows(GeocodingException.class, () -> parser.parseCoordinates(response));
    }

}