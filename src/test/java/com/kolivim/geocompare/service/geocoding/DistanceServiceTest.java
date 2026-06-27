package com.kolivim.geocompare.service.geocoding;

import com.kolivim.geocompare.dto.response.CoordinatesDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceServiceTest {

    private final DistanceService distanceService = new DistanceService();


    @Test
    @DisplayName("Проверка правильности расчета расстояния")
    void calculateDistanceReturnCorrectDistance() {

        /**
         * Москва, Красная площадь, 1
         * yandexCoordinates: "lat": 55.755277, "lon": 37.61768
         * dadataCoordinates: "lat": 55.7552921, "lon": 37.6176294
         * distanceMeters": 3.5838294775952764
        */

        double lat1 = 55.755277;
        double lon1 = 37.61768;
        double lat2 = 55.7552921;
        double lon2 = 37.6176294;

        double expectedDistance = 3.5838294775952764;

        double actual = distanceService.calculateDistance(lat1, lon1, lat2, lon2);

        assertEquals(expectedDistance, actual, 0);
    }


    @Test
    @DisplayName("Разных точек координаты переданы")
    void calculateDistanceNotCorrectDistance() {

        double lat1 = 55.755277;
        double lon1 = 37.6208;
        double lat2 = 59.9391;
        double lon2 = 30.3159;

        double expectedDistance = 3.5838294775952764;

        double actual = distanceService.calculateDistance(lat1, lon1, lat2, lon2);

        assertNotEquals(expectedDistance, actual, 0);
    }

}