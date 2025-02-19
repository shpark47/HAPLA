package com.hapla.flights.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;

@RestController
@RequestMapping("/flight")
public class FlightController {

    private static final String GEONAMES_USERNAME = "seonghyeon"; // GeoNames 사용자명 입력

    @GetMapping("/iataSearch")
    public ResponseEntity<List<Map<String, String>>> iataSearch(@RequestParam("value") String value) {
        if (value == null || value.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // 입력값이 없으면 빈 리스트 반환
        }

        String url = "http://api.geonames.org/searchJSON?q=" + value + "&featureClass=A&featureClass=P&maxRows=10&username=" + GEONAMES_USERNAME;
        
        RestTemplate restTemplate = new RestTemplate();
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> geonames = (List<Map<String, Object>>) response.get("geonames");

            List<Map<String, String>> results = new ArrayList<>();
            for (Map<String, Object> item : geonames) {
                Map<String, String> airportData = new HashMap<>();
                airportData.put("iata", (String) item.getOrDefault("iata", "")); // IATA 코드
                airportData.put("nameEn", (String) item.get("name")); // 공항 영문명
                airportData.put("nameKo", (String) item.getOrDefault("asciiName", "")); // 한글명 (없을 경우 영문명 대체)
                airportData.put("country", (String) item.get("countryName")); // 국가명
                results.add(airportData);
            }

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
