package com.kolivim.geocompare.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${dadata.api.key}")
    private String dadataApiKey;

    @Bean(name = "yandexWebClient")
    public WebClient yandexWebClient(@Value("${yandex.api.geocode-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }

    @Bean(name = "dadataWebClient")
    @Primary
    public WebClient dadataWebClient(@Value("${dadata.api.geocode-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Token " + dadataApiKey)
                .build();
    }

}