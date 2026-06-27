package com.kolivim.geocompare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolivim.geocompare.dto.request.AddressRequest;
import com.kolivim.geocompare.dto.response.ComparisonResult;
import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.service.GeoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeoService geoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Возврат расстояния при успешном выполнении геокодирования")
    void compareGeocodingReturnComparisonResult() throws Exception {
        AddressRequest request = new AddressRequest("Москва, Красная площадь, 1");
        CoordinatesDto yandex = new CoordinatesDto(55.7539, 37.6208);
        CoordinatesDto dadata = new CoordinatesDto(55.7540, 37.6210);
        ComparisonResult result = ComparisonResult.builder()
                .address(request.getAddress())
                .yandexCoordinates(yandex)
                .dadataCoordinates(dadata)
                .distanceMeters(15.0)
                .timestamp("2025-01-01T00:00:00")
                .build();

        when(geoService.processAddress(any(AddressRequest.class))).thenReturn(result);

        mockMvc.perform(post("/api/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(request.getAddress()))
                .andExpect(jsonPath("$.yandexCoordinates.lat").value(55.7539))
                .andExpect(jsonPath("$.dadataCoordinates.lon").value(37.6210))
                .andExpect(jsonPath("$.distanceMeters").value(15.0));
    }

    @DisplayName("Возврат ошибки при не успешном выполнении геокодирования")
    @Test
    void compareGeocodingReturnBadRequestWhenAddressInvalid() throws Exception {
        AddressRequest request = new AddressRequest("short");

        mockMvc.perform(post("/api/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

}