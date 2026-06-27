package com.kolivim.geocompare.service.geocoding;

import com.kolivim.geocompare.dto.response.CoordinatesDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DistanceService {

    private static final double EARTH_RADIUS_M = 6_371_000;

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        log.info("startMethod point1({},{}), point2({},{})", lat1, lon1, lat2, lon2);
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_M * c;
        log.info("endMethod, к возврату расстояние: {} meters", distance);
        return distance;
    }

    public double calculateDistance(CoordinatesDto firstCoords,CoordinatesDto secondCoords) {
        log.info("startMethod firstCoords: {}, secondCoords: {}", firstCoords, secondCoords);
        return calculateDistance(
                firstCoords.getLat(), firstCoords.getLon(),
                secondCoords.getLat(), secondCoords.getLon()
        );
    }

}