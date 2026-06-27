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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeoService {

    private final YandexGeocodingService yandexGeocodingService;

    private final DadataGeocodingService dadataGeocodingService;

    private final DistanceService distanceService;

    private final AddressComparisonRepository repository;

    private final GeoMapper geoMapper;


    public ComparisonResult processAddress(AddressRequest request) {
        log.info("startMethod address={}", request.getAddress());

        CoordinatesDto yandexCoords;
        CoordinatesDto dadataCoords;

        yandexCoords = yandexGeocodingService.getCoordinates(request.getAddress());
        dadataCoords = dadataGeocodingService.getCoordinates(request.getAddress());

        double distance = distanceService.calculateDistance(yandexCoords, dadataCoords);

        AddressComparison entity = repository.save(
                geoMapper.toAddressComparison(request, yandexCoords, dadataCoords, distance));

        return geoMapper.toComparisonResult(entity, yandexCoords, dadataCoords);
    }

}