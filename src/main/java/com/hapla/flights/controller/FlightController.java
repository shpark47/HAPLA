package com.hapla.flights.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@RequestMapping("/flight")
public class FlightController {

    private static final List<Map<String, String>> koreanAirports = initializeKoreanAirports();

    @GetMapping("/iataSearch")
    public ResponseEntity<?> iataSearch(@RequestParam("value") String searchValue) {
        try {
            List<Map<String, String>> results = searchKoreanAirports(searchValue);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private List<Map<String, String>> searchKoreanAirports(String searchValue) {
        List<Map<String, String>> results = new ArrayList<>();
        String lowerSearchValue = searchValue.toLowerCase();

        for (Map<String, String> airport : koreanAirports) {
            if (airport.get("nameKo").toLowerCase().contains(lowerSearchValue) ||
                airport.get("nameEn").toLowerCase().contains(lowerSearchValue) ||
                airport.get("iata").toLowerCase().contains(lowerSearchValue)) {
                results.add(airport);
            }
        }

        return results;
    }

    private static List<Map<String, String>> initializeKoreanAirports() {
        List<Map<String, String>> airports = new ArrayList<>();
        
        addAirport(airports, "서울", "Seoul", "SEL", "city", "대한민국");
        addAirport(airports, "인천국제공항", "Incheon International Airport", "ICN", "airport", "대한민국");
        addAirport(airports, "김포국제공항", "Gimpo International Airport", "GMP", "airport", "대한민국");
        addAirport(airports, "제주국제공항", "Jeju International Airport", "CJU", "airport", "대한민국");
        addAirport(airports, "부산", "Busan", "PUS", "city", "대한민국");
        addAirport(airports, "김해국제공항", "Gimhae International Airport", "PUS", "airport", "대한민국");
        addAirport(airports, "대구국제공항", "Daegu International Airport", "TAE", "airport", "대한민국");
        addAirport(airports, "청주국제공항", "Cheongju International Airport", "CJJ", "airport", "대한민국");
        addAirport(airports, "무안국제공항", "Muan International Airport", "MWX", "airport", "대한민국");
        addAirport(airports, "양양국제공항", "Yangyang International Airport", "YNY", "airport", "대한민국");
        addAirport(airports, "광주공항", "Gwangju Airport", "KWJ", "airport", "대한민국");
        addAirport(airports, "여수공항", "Yeosu Airport", "RSU", "airport", "대한민국");
        addAirport(airports, "울산공항", "Ulsan Airport", "USN", "airport", "대한민국");
        addAirport(airports, "포항경주공항", "Pohang Airport", "KPO", "airport", "대한민국");
        addAirport(airports, "사천공항", "Sacheon Airport", "HIN", "airport", "대한민국");
        addAirport(airports, "군산공항", "Gunsan Airport", "KUV", "airport", "대한민국");
        addAirport(airports, "원주공항", "Wonju Airport", "WJU", "airport", "대한민국");

        return airports;
    }

    private static void addAirport(List<Map<String, String>> airports, String nameKo, String nameEn, String iata, String type, String country) {
        Map<String, String> airport = new HashMap<>();
        airport.put("nameKo", nameKo);
        airport.put("nameEn", nameEn);
        airport.put("iata", iata);
        airport.put("type", type);
        airport.put("country", country);
        airports.add(airport);
    }
}