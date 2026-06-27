package com.kolivim.geocompare.service;

import com.kolivim.geocompare.dto.request.AddressRequest;
import com.kolivim.geocompare.dto.response.ComparisonResult;
import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.entity.AddressComparison;
import com.kolivim.geocompare.mapper.GeoMapper;
import com.kolivim.geocompare.repository.AddressComparisonRepository;
import com.kolivim.geocompare.service.geocoding.DadataGeocodingService;
import com.kolivim.geocompare.service.geocoding.DistanceService;
import com.kolivim.geocompare.service.geocoding.YandexGeocodingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoServiceTest {

    @Mock
    private YandexGeocodingService yandexGeocodingService;

    @Mock
    private DadataGeocodingService dadataGeocodingService;

    @Mock
    private DistanceService distanceService;

    @Mock
    private AddressComparisonRepository repository;

    @Mock
    private GeoMapper geoMapper;

    @InjectMocks
    private GeoService geoService;

    @Test
    @DisplayName("Корректное получение расстояния между двумя парами координат")
    void processAddressReturnComparisonResult() {
        String address = "Москва, Красная площадь, 1";
        AddressRequest request = new AddressRequest(address);
        CoordinatesDto yandexCoords = new CoordinatesDto(55.7539, 37.6208);
        CoordinatesDto dadataCoords = new CoordinatesDto(55.7540, 37.6210);
        double distance = 15.0;

        AddressComparison entity = AddressComparison.builder()
                .id(1L)
                .address(address)
                .yandexLat(yandexCoords.getLat())
                .yandexLon(yandexCoords.getLon())
                .dadataLat(dadataCoords.getLat())
                .dadataLon(dadataCoords.getLon())
                .distanceMeters(distance)
                .build();

        ComparisonResult expectedResult = ComparisonResult.builder()
                .address(address)
                .yandexCoordinates(yandexCoords)
                .dadataCoordinates(dadataCoords)
                .distanceMeters(distance)
                .timestamp("2025-01-01T00:00:00")
                .build();

        when(yandexGeocodingService.getCoordinates(address)).thenReturn(yandexCoords);
        when(dadataGeocodingService.getCoordinates(address)).thenReturn(dadataCoords);
        when(distanceService.calculateDistance(yandexCoords, dadataCoords)).thenReturn(distance);
        when(geoMapper.toAddressComparison(request, yandexCoords, dadataCoords, distance)).thenReturn(entity);
        when(repository.save(any(AddressComparison.class))).thenReturn(entity);
        when(geoMapper.toComparisonResult(entity, yandexCoords, dadataCoords)).thenReturn(expectedResult);

        ComparisonResult result = geoService.processAddress(request);

        assertNotNull(result);
        assertEquals(address, result.getAddress());
        assertEquals(distance, result.getDistanceMeters());
        verify(repository).save(any(AddressComparison.class));
    }

}