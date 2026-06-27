package com.kolivim.geocompare.service.geocoding;

import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.dto.external.DadataGeocoderResponse;
import com.kolivim.geocompare.exception.GeocodingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DadataResponseParser {

    public CoordinatesDto parseCoordinates(DadataGeocoderResponse response) {
        log.info("startMethod Dadata response: {}", response);

        if (response == null || response.getSuggestions() == null || response.getSuggestions().isEmpty()) {
            throw new GeocodingException("Dadata не вернул ни одной подсказки");
        }

        DadataGeocoderResponse.Suggestion suggestion = response.getSuggestions().get(0);
        if (suggestion.getData() == null ||
                suggestion.getData().getGeoLat() == null ||
                suggestion.getData().getGeoLon() == null) {
            throw new GeocodingException("Dadata не вернул координаты");
        }

        double lat;
        double lon;
        try {
            lat = Double.parseDouble(suggestion.getData().getGeoLat());
            lon = Double.parseDouble(suggestion.getData().getGeoLon());
        } catch (NumberFormatException e) {
            throw new GeocodingException("Нечисловые координаты Dadata: lat=" +
                    suggestion.getData().getGeoLat() + ", lon=" + suggestion.getData().getGeoLon());
        }

        log.info("endMethod Dadata, к возврату lat={}, lon={}", lat, lon);
        return new CoordinatesDto(lat, lon);
    }

}