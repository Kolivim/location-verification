package com.kolivim.geocompare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonResult {

    private String address;

    private CoordinatesDto yandexCoordinates;

    private CoordinatesDto dadataCoordinates;

    private double distanceMeters;

    private String timestamp;

}