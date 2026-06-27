package com.kolivim.geocompare.service.geocoding;

import com.kolivim.geocompare.dto.external.YandexGeocoderResponse;
import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.exception.GeocodingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class YandexGeocodingServiceTest {

    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @Mock
    private YandexResponseParser yandexResponseParser;

    @InjectMocks
    private YandexGeocodingService yandexGeocodingService;

    @BeforeEach
    void setUp() {
        lenient().when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        lenient().when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        lenient().when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        lenient().when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
    }


    @Test
    @DisplayName("Корректное получение координат")
    void getCoordinatesReturnCoordinatesWhenValidResponse() {
        String address = "Москва, Красная площадь, 1";
        CoordinatesDto expectedCoords = new CoordinatesDto(55.7539, 37.6208);
        YandexGeocoderResponse dummyResponse = new YandexGeocoderResponse();

        when(responseSpecMock.bodyToMono(YandexGeocoderResponse.class))
                .thenReturn(Mono.just(dummyResponse));
        when(yandexResponseParser.extractCoordinates(dummyResponse))
                .thenReturn(expectedCoords);

        CoordinatesDto result = yandexGeocodingService.getCoordinates(address);
        assertEquals(expectedCoords, result);
    }

    @Test
    @DisplayName("Проверка выброса исключения GeocodingException при вызове bodyToMono(например, ошибка сети)")
    void getCoordinatesThrowGeocodingExceptionWhenApiFails() {
        String address = "Invalid address";

        when(responseSpecMock.bodyToMono(YandexGeocoderResponse.class))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        assertThrows(GeocodingException.class,
                () -> yandexGeocodingService.getCoordinates(address));
    }

}