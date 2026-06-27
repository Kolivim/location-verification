package com.kolivim.geocompare.service.geocoding;

import com.kolivim.geocompare.dto.external.DadataGeocoderResponse;
import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.exception.GeocodingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DadataGeocodingServiceTest {

    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @Mock
    private DadataResponseParser dadataResponseParser;

    @InjectMocks
    private DadataGeocodingService dadataGeocodingService;

    @BeforeEach
    void setUp() {
        lenient().doReturn(requestBodyUriSpecMock).when(webClientMock).post();
        lenient().doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).contentType(MediaType.APPLICATION_JSON);
        lenient().doReturn(requestBodySpecMock).when(requestBodySpecMock).bodyValue(any());
        lenient().doReturn(responseSpecMock).when(requestBodySpecMock).retrieve();
        lenient().doReturn(responseSpecMock).when(responseSpecMock).onStatus(any(), any());
    }

    @Test
    @DisplayName("Корректное получение координат")
    void getCoordinates_shouldReturnCoordinates_whenValidResponse() {
        String address = "Москва, Красная площадь, 1";
        CoordinatesDto expectedCoords = new CoordinatesDto(55.7539, 37.6208);
        DadataGeocoderResponse dummyResponse = new DadataGeocoderResponse();

        when(responseSpecMock.bodyToMono(DadataGeocoderResponse.class))
                .thenReturn(Mono.just(dummyResponse));
        when(dadataResponseParser.parseCoordinates(dummyResponse))
                .thenReturn(expectedCoords);

        CoordinatesDto result = dadataGeocodingService.getCoordinates(address);
        assertEquals(expectedCoords, result);
    }

    @Test
    @DisplayName("Проверка выброса исключения GeocodingException при некорректном переданном адресе")
    void getCoordinatesThrowGeocodingExceptionWhenApiFails() {
        String address = "Invalid address";

        when(responseSpecMock.bodyToMono(DadataGeocoderResponse.class))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        assertThrows(GeocodingException.class,
                () -> dadataGeocodingService.getCoordinates(address));
    }

}