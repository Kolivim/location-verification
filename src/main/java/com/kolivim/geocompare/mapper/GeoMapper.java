package com.kolivim.geocompare.mapper;

import com.kolivim.geocompare.dto.request.AddressRequest;
import com.kolivim.geocompare.dto.response.ComparisonResult;
import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.entity.AddressComparison;
import org.springframework.stereotype.Component;

@Component
public class GeoMapper {

    public AddressComparison toAddressComparison(AddressRequest request,
                                                 CoordinatesDto yandexCoords,
                                                 CoordinatesDto dadataCoords,
                                                 double distance) {
        return AddressComparison.builder()
                .address(request.getAddress())
                .yandexLat(yandexCoords.getLat())
                .yandexLon(yandexCoords.getLon())
                .dadataLat(dadataCoords.getLat())
                .dadataLon(dadataCoords.getLon())
                .distanceMeters(distance)
                .build();
    }

    public ComparisonResult toComparisonResult(AddressComparison entity,
                                               CoordinatesDto yandexCoords,
                                               CoordinatesDto dadataCoords) {
        return ComparisonResult.builder()
                .address(entity.getAddress())
                .yandexCoordinates(yandexCoords)
                .dadataCoordinates(dadataCoords)
                .distanceMeters(entity.getDistanceMeters())
                .timestamp(entity.getCreatedAt().toString())
                .build();
    }

}