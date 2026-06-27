package com.kolivim.geocompare.controller;

import com.kolivim.geocompare.dto.request.AddressRequest;
import com.kolivim.geocompare.dto.response.ComparisonResult;
import com.kolivim.geocompare.service.GeoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {

    private final GeoService geoService;

    @PostMapping(value ="/address", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComparisonResult> compareGeocoding(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok( geoService.processAddress(request));
    }

}