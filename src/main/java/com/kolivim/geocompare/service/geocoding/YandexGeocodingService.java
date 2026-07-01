package com.kolivim.geocompare.service.geocoding;

import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.dto.external.YandexGeocoderResponse;
import com.kolivim.geocompare.exception.GeocodingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class YandexGeocodingService {

    private final WebClient webClient;

    private final YandexResponseParser yandexResponseParser;

    @Value("${yandex.api.key}")
    private String apiKey;


    public YandexGeocodingService(@Qualifier("yandexWebClient") WebClient webClient,
                                  @Value("${yandex.api.key}") String apiKey,
                                  YandexResponseParser yandexResponseParser) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.yandexResponseParser = yandexResponseParser;
    }


    public CoordinatesDto getCoordinates(String address) {
        log.info("startMethod address='{}'", address);

        YandexGeocoderResponse response;

        try {

            response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("apikey", apiKey)
                            .queryParam("geocode", address)
                            .queryParam("format", "json")
                            .queryParam("results", "1")
                            .build())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new RuntimeException("Yandex API ошибка вызова"))))
                    .bodyToMono(YandexGeocoderResponse.class)
                    .block();

        } catch (Exception e) {
            log.error("Ошибка Yandex geocoding {}", e.getMessage());
            throw new GeocodingException("Yandex geocoding error: " + e.getMessage());
        }

        return yandexResponseParser.extractCoordinates(response);
    }

}