package com.kolivim.geocompare.integration;

import com.kolivim.geocompare.dto.request.AddressRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class AddressControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("geocompare_test")
            .withUsername("test")
            .withPassword("test");

    private static MockWebServer yandexMockServer;
    private static MockWebServer dadataMockServer;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("yandex.api.geocode-url", () -> "http://localhost:" + yandexMockServer.getPort() + "/yandex");
        registry.add("dadata.api.geocode-url", () -> "http://localhost:" + dadataMockServer.getPort() + "/dadata");
    }

    @BeforeAll
    static void startMockServers() throws IOException {
        yandexMockServer = new MockWebServer();
        yandexMockServer.start();
        dadataMockServer = new MockWebServer();
        dadataMockServer.start();
    }

    @AfterAll
    static void stopMockServers() throws IOException {
        yandexMockServer.shutdown();
        dadataMockServer.shutdown();
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Успешное выполнение")
    void compareGeocodingReturnSuccess() {

        yandexMockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "response": {
                                "GeoObjectCollection": {
                                    "featureMember": [
                                        {
                                            "GeoObject": {
                                                "Point": {
                                                    "pos": "37.6208 55.7539"
                                                }
                                            }
                                        }
                                    ]
                                }
                            }
                        }
                        """));


        dadataMockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "suggestions": [
                                {
                                    "data": {
                                        "geo_lat": "55.7540",
                                        "geo_lon": "37.6210"
                                    }
                                }
                            ]
                        }
                        """));


        AddressRequest request = new AddressRequest("Москва, Красная площадь, 1");
        ResponseEntity<String> response = restTemplate.postForEntity("/api/address", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("55.7539");
        assertThat(response.getBody()).contains("37.6208");
        assertThat(response.getBody()).contains("55.754");
        assertThat(response.getBody()).contains("37.621");
        assertThat(response.getBody()).contains("\"distanceMeters\":");
        assertThat(response.getBody()).contains("16.74");
    }

    @Test
    @DisplayName("Получение ошибки при выполнении")
    void compareGeocodingReturnBadGatewayWhenYandexFails() {
        yandexMockServer.enqueue(new MockResponse().setResponseCode(500));
        dadataMockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "suggestions": [
                                {
                                    "data": {
                                        "geo_lat": "55.7540",
                                        "geo_lon": "37.6210"
                                    }
                                }
                            ]
                        }
                        """));

        AddressRequest request = new AddressRequest("Москва, Красная площадь, 1");
        ResponseEntity<String> response = restTemplate.postForEntity("/api/address", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).contains("Geocoding Error");
    }

}