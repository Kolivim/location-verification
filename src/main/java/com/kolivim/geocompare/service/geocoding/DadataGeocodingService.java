package com.kolivim.geocompare.service.geocoding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.dto.external.DadataGeocoderResponse;
import com.kolivim.geocompare.exception.GeocodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DadataGeocodingService {

    private final WebClient webClient;

    private final DadataResponseParser dadataResponseParser;

    private final ObjectMapper mapper = new ObjectMapper();


    public CoordinatesDto getCoordinates(String address) {
        log.info("startMethod получен address='{}'", address);

        final DadataGeocoderResponse response;
        try {

            response = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(mapper.writeValueAsString(Map.of("query", address, "count", 1)))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new RuntimeException("HTTP " + clientResponse.statusCode()))))
                    .bodyToMono(DadataGeocoderResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Ошибка Dadata geocoding {}", e.getMessage());
            throw new GeocodingException("Dadata geocoding error: " + e.getMessage());
        }

        return dadataResponseParser.parseCoordinates(response);

    }

}