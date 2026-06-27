package com.kolivim.geocompare.service.geocoding;

import com.kolivim.geocompare.dto.response.CoordinatesDto;
import com.kolivim.geocompare.dto.external.YandexGeocoderResponse;
import com.kolivim.geocompare.exception.GeocodingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class YandexResponseParser {

    public CoordinatesDto extractCoordinates(YandexGeocoderResponse response) {
        log.info("startMethod YandexGeocoderResponse response: {}", response);

        if (response == null || response.getResponse() == null) {
            throw new GeocodingException("Пустой ответ от Яндекса");
        }

        var geoObjectCollection = response.getResponse().getGeoObjectCollection();
        if (geoObjectCollection == null) {
            throw new GeocodingException("Ответ Яндекса не содержит GeoObjectCollection");
        }

        var members = geoObjectCollection.getFeatureMember();
        if (members == null || members.isEmpty()) {
            throw new GeocodingException("Яндекс не вернул ни одного объекта");
        }

        var firstMember = members.get(0);
        if (firstMember.getGeoObject() == null) {
            throw new GeocodingException("Ответ Яндекса не содержит GeoObject в первом элементе");
        }

        var point = firstMember.getGeoObject().getPoint();
        if (point == null) {
            throw new GeocodingException("Ответ Яндекса не содержит Point");
        }

        String pos = point.getPos();
        if (pos == null || pos.isBlank()) {
            throw new GeocodingException("Яндекс не вернул координаты (pos)");
        }

        String[] coords = pos.split(" ");
        if (coords.length != 2) {
            throw new GeocodingException("Некорректный формат координат: " + pos);
        }

        double lon, lat;
        try {
            lon = Double.parseDouble(coords[0]);
            lat = Double.parseDouble(coords[1]);
        } catch (NumberFormatException e) {
            throw new GeocodingException("Нечисловые координаты: " + pos);
        }

        log.info("endMethod, к возврату lat={}, lon={}", lat, lon);
        return new CoordinatesDto(lat, lon);
    }

}