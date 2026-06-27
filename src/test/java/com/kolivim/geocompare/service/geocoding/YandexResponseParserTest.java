package com.kolivim.geocompare.service.geocoding;

import com.kolivim.geocompare.dto.external.YandexGeocoderResponse;
import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.exception.GeocodingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class YandexResponseParserTest {

    private final YandexResponseParser parser = new YandexResponseParser();

    @Test
    @DisplayName("Проверка правильности парсинга координат")
    void extractCoordinatesReturnCoordinatesWhenValidResponse() {
        YandexGeocoderResponse response = new YandexGeocoderResponse();
        YandexGeocoderResponse.Response resp = new YandexGeocoderResponse.Response();
        YandexGeocoderResponse.GeoObjectCollection collection = new YandexGeocoderResponse.GeoObjectCollection();
        YandexGeocoderResponse.FeatureMember member = new YandexGeocoderResponse.FeatureMember();
        YandexGeocoderResponse.GeoObject geoObject = new YandexGeocoderResponse.GeoObject();
        YandexGeocoderResponse.Point point = new YandexGeocoderResponse.Point();
        point.setPos("37.6208 55.7539");
        geoObject.setPoint(point);
        member.setGeoObject(geoObject);
        collection.setFeatureMember(java.util.List.of(member));
        resp.setGeoObjectCollection(collection);
        response.setResponse(resp);

        CoordinatesDto coords = parser.extractCoordinates(response);
        assertEquals(55.7539, coords.getLat());
        assertEquals(37.6208, coords.getLon());
    }

    @Test
    @DisplayName("Проверка выброса исключения при переданном Null")
    void extractCoordinatesThrowExceptionWhenResponseIsNull() {
        assertThrows(GeocodingException.class, () -> parser.extractCoordinates(null));
    }

    @Test
    @DisplayName("Проверка выброса исключения GeocodingException, при отсутствии FeatureMember")
    void extractCoordinatesThrowExceptionWhenNoFeatureMembers() {
        YandexGeocoderResponse response = new YandexGeocoderResponse();
        YandexGeocoderResponse.Response resp = new YandexGeocoderResponse.Response();
        YandexGeocoderResponse.GeoObjectCollection collection = new YandexGeocoderResponse.GeoObjectCollection();
        collection.setFeatureMember(java.util.List.of());
        resp.setGeoObjectCollection(collection);
        response.setResponse(resp);

        assertThrows(GeocodingException.class, () -> parser.extractCoordinates(response));
    }

    @Test
    @DisplayName("Проверка выброса исключения GeocodingException")
    void extractCoordinatesThrowExceptionWhenPosIsMalformed() {
        YandexGeocoderResponse response = new YandexGeocoderResponse();
        YandexGeocoderResponse.Response resp = new YandexGeocoderResponse.Response();
        YandexGeocoderResponse.GeoObjectCollection collection = new YandexGeocoderResponse.GeoObjectCollection();
        YandexGeocoderResponse.FeatureMember member = new YandexGeocoderResponse.FeatureMember();
        YandexGeocoderResponse.GeoObject geoObject = new YandexGeocoderResponse.GeoObject();
        YandexGeocoderResponse.Point point = new YandexGeocoderResponse.Point();
        point.setPos("invalid");
        geoObject.setPoint(point);
        member.setGeoObject(geoObject);
        collection.setFeatureMember(java.util.List.of(member));
        resp.setGeoObjectCollection(collection);
        response.setResponse(resp);

        assertThrows(GeocodingException.class, () -> parser.extractCoordinates(response));
    }

}