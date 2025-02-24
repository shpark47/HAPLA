package com.hapla.flights.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.hapla.flights.model.vo.Airport;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Controller
@RequestMapping("/flight")
public class FlightController {

    private List<Airport> airports = loadCSV();
    @Value("${AMADEUS.API.ID}")
    private String AMADEUS_API_ID;
    @Value("${AMADEUS.API.KEY}")
    private String AMADEUS_API_KEY;
    @Value("${TAGO.API.KEY}")
    private String TAGO_API_KEY;

    public List<Airport> loadCSV() {
        List<Airport> airports = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/static/csv/airports.csv"))) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                Airport airport = new Airport();
                airport.setIataCode(values[0]);
                airport.setAirportsEnName(values[1]);
                airport.setLatitude(values[2]);
                airport.setLongitude(values[3]);
                airport.setCityCode(values[4]);
                airport.setAirportsKoName(values[5]);

                airports.add(airport);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return airports;
    }

    @GetMapping(value = "/search", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<Airport> searchAirports(@RequestParam("query") String query) {
        return airports.stream()
                .filter(airport -> airport.getAirportsKoName().toLowerCase().contains(query.toLowerCase())) // 이름 필터링
                .limit(5) // 최대 5개 결과 반환
                .collect(Collectors.toList()); // 전체 Airport 객체 반환
    }

    @GetMapping("/flightSearch")
    public String flightSearch(@RequestParam("departureName") String departure,
            @RequestParam("arrivalName") String arrival, @RequestParam("dates") String dates,
            @RequestParam("travelers") String travelers, Model model) {

        // Add initial debug logging
        System.out.println("=== Flight Search Started ===");
        System.out.println("Input parameters:");
        System.out.println("Departure: " + departure);
        System.out.println("Arrival: " + arrival);
        System.out.println("Dates: " + dates);
        System.out.println("Travelers: " + travelers);

        try {
            // IATA code extraction
        	String iataPattern = "\\((\\w{3})\\)";
            Pattern pattern = Pattern.compile(iataPattern);
            Matcher departureMatcher = pattern.matcher(departure);
            Matcher arrivalMatcher = pattern.matcher(arrival);

            if (!departureMatcher.find() || !arrivalMatcher.find()) {
                System.out.println("Failed to extract IATA codes");
                model.addAttribute("error", "출발지와 도착지에서 IATA 코드를 찾을 수 없습니다.");
                return "flightSearchResult";
            }

            String departureCode = departureMatcher.group(1);
            String arrivalCode = arrivalMatcher.group(1);
            
            System.out.println("Extracted IATA codes - Departure: " + departureCode + ", Arrival: " + arrivalCode);

            // Date parsing
            String[] dateSplit = dates.split(" ~ ");
            String departureDate = dateSplit[0];
            String returnDate = (dateSplit.length > 1) ? dateSplit[1] : null;
            
            System.out.println("Parsed dates - Departure: " + departureDate + ", Return: " + returnDate);

            // Check if domestic flight
            boolean isDomestic = isDomesticFlight(departureCode, arrivalCode);
            System.out.println("Is domestic flight: " + isDomestic);

            // API key verification
            System.out.println("API Keys present:");
            System.out.println("TAGO_API_KEY: " + TAGO_API_KEY);
            System.out.println("AMADEUS_API_ID: " + AMADEUS_API_ID  + (AMADEUS_API_ID != null && !AMADEUS_API_ID.isEmpty()));
            System.out.println("AMADEUS_API_KEY: " + AMADEUS_API_KEY +  (AMADEUS_API_KEY != null && !AMADEUS_API_KEY.isEmpty()));

            List<Map<String, String>> flightOffers;
            if (isDomestic) {
                flightOffers = getDomesticFlightOffers(departureCode, arrivalCode, departureDate, returnDate, travelers);
                System.out.println("<<<<<<<<<" + flightOffers);
            } else {
                String accessToken = getAmadeusAccessToken();
                if (accessToken == null) {
                    System.out.println("Failed to get Amadeus access token");
                    model.addAttribute("error", "국제선 항공권 검색 실패: 인증 오류");
                    return "flightSearchResult";
                }
                flightOffers = getFlightOffers(accessToken, departureCode, arrivalCode, departureDate, returnDate, travelers);
            }

            System.out.println("Flight offers found: " + (flightOffers != null ? flightOffers.size() : "null"));
            if (flightOffers != null && !flightOffers.isEmpty()) {
                System.out.println("First flight offer: " + flightOffers.get(0));
            }

            model.addAttribute("flightOffers", flightOffers);
            return "flightSearchResult";

        } catch (Exception e) {
            System.out.println("Exception in flight search: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "항공권 검색 중 오류가 발생했습니다: " + e.getMessage());
            return "flightSearchResult";
        }
    }

    // Helper method to determine if flight is domestic
    private boolean isDomesticFlight(String departureCode, String arrivalCode) {
        // List of Korean airport codes
        List<String> koreanAirports = Arrays.asList("ICN", "GMP", "PUS", "CJU", "TAE", "KWJ", "RSU", "KPO", "WJU",
                "USN", "HIN", "YNY");
        return koreanAirports.contains(departureCode) && koreanAirports.contains(arrivalCode);
    }

    private String getAirportId(String iataCode) {
        Map<String, String> airportIdMap = new HashMap<>();
        airportIdMap.put("ICN", "NAARKSI"); // 인천국제공항
        airportIdMap.put("GMP", "NAARKSS"); // 김포공항
        airportIdMap.put("PUS", "NAARKPK"); // 부산김해공항
        airportIdMap.put("CJU", "NAARKPC"); // 제주공항
        airportIdMap.put("TAE", "NAARKTN"); // 대구공항
        airportIdMap.put("KWJ", "NAARKJJ"); // 광주공항
        airportIdMap.put("RSU", "NAARKJY"); // 여수공항
        airportIdMap.put("KPO", "NAARKTH"); // 포항공항 (추정 ID, 실제 확인 필요)
        airportIdMap.put("WJU", "NAARKNW"); // 원주공항 (추정 ID, 실제 확인 필요)
        airportIdMap.put("USN", "NAARKPU"); // 울산공항
        airportIdMap.put("HIN", "NAARKPS"); // 진주공항
        airportIdMap.put("YNY", "NAARKNY"); // 양양공항

        return airportIdMap.getOrDefault(iataCode, "UNKNOWN"); // 해당하는 공항 ID가 없으면 "UNKNOWN" 반환
    }

    // Amadeus API 토큰 요청
    private String getAmadeusAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://test.api.amadeus.com/v1/security/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials&client_id=" + AMADEUS_API_ID + "&client_secret=" + AMADEUS_API_KEY;
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                return json.getString("access_token");
            }
        } catch (Exception e) {
            System.out.println("Amadeus Token Request Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private List<Map<String, String>> getDomesticFlightOffers(String departure, String arrival, String departureDate, String returnDate, String travelers) {
        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, String>> results = new ArrayList<>();

        try {
            String url = "http://apis.data.go.kr/1613000/DmstcFlightNvgInfoService/getFlightOpratInfoList" 
                    + "?serviceKey=" + TAGO_API_KEY 
                 
                    + "&_type=json" 
                    + "&depAirportId=" + URLEncoder.encode(getAirportId(departure), "UTF-8") 
                    + "&arrAirportId=" + URLEncoder.encode(getAirportId(arrival), "UTF-8") 
                    + "&depPlandTime=" + URLEncoder.encode(departureDate.replace("-", ""), "UTF-8");

            System.out.println("Domestic Flight API URL: " + url); // URL을 확인할 수 있도록 출력

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println("Domestic API Response: " + response.getBody()); // 응답 출력

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                JSONObject responseObj = json.getJSONObject("response");
                JSONObject bodyObj = responseObj.getJSONObject("body");
                JSONObject itemsObj = bodyObj.getJSONObject("items");

                JSONArray items;
                if (itemsObj.has("item") && itemsObj.get("item") instanceof JSONObject) {
                    // 단일 객체인 경우 JSONArray로 변환
                    items = new JSONArray().put(itemsObj.getJSONObject("item"));
                } else {
                    items = itemsObj.optJSONArray("item");
                }

                if (items == null || items.length() == 0) {
                    System.out.println("No domestic flight offers found.");
                    return results;
                }

                for (int i = 0; i < items.length(); i++) {
                    JSONObject flight = items.getJSONObject(i);
                    Map<String, String> flightData = new HashMap<>();

                    // economyCharge가 존재하는지 확인하고, 없으면 기본값 사용
                    double priceKRW = flight.has("economyCharge") ? flight.getDouble("economyCharge") : 0.0;
                    double priceEUR = priceKRW / 1300.0;

                    // 결과에 가격 정보 추가
                    flightData.put("price", priceKRW > 0 ? String.format("%.2f EUR (₩%,.0f)", priceEUR, priceKRW) : "가격 정보 없음");
                    flightData.put("airline", flight.getString("airlineNm"));
                    flightData.put("flightNumber", flight.getString("vihicleId"));
                    flightData.put("departureTime", formatTAGODateTime(String.valueOf(flight.getLong("depPlandTime"))));
                    flightData.put("arrivalTime", formatTAGODateTime(String.valueOf(flight.getLong("arrPlandTime"))));
                    flightData.put("departureAirport", departure);
                    flightData.put("arrivalAirport", arrival);
                    flightData.put("isDomestic", "true");

                    results.add(flightData);
                }

            } else {
                System.out.println("Domestic API Error: Status " + response.getStatusCode() + ", Body: " + response.getBody());
            }
        } catch (Exception e) {
            System.out.println("Domestic API Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }


    // Helper method to format TAGO API datetime
    private String formatTAGODateTime(String tagoDateTime) {
        // Convert YYYYMMDDHHMM to YYYY-MM-DDTHH:mm:00
        return tagoDateTime.substring(0, 4) + "-" + tagoDateTime.substring(4, 6) + "-" + tagoDateTime.substring(6, 8)
                + "T" + tagoDateTime.substring(8, 10) + ":" + tagoDateTime.substring(10, 12) + ":00";
    }

    private List<Map<String, String>> getFlightOffers(String accessToken, String departure, String arrival,
            String departureDate, String returnDate, String travelers) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://test.api.amadeus.com/v2/shopping/flight-offers" 
                + "?originLocationCode=" + departure
                + "&destinationLocationCode=" + arrival 
                + "&departureDate=" + departureDate 
                + "&returnDate=" + (returnDate != null ? returnDate : "") 
                + "&adults=" + travelers 
                + "&max=10";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        List<Map<String, String>> results = new ArrayList<>();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            System.out.println("Amadeus API URL: " + url); // 요청 URL 출력
            System.out.println("Amadeus API Response: " + response.getBody()); // 응답 출력
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                JSONArray flights = json.getJSONArray("data");
                System.out.println("Number of flights: " + flights.length());

                for (int i = 0; i < flights.length(); i++) {
                    JSONObject flight = flights.getJSONObject(i);
                    JSONObject itinerary = flight.getJSONArray("itineraries").getJSONObject(0);
                    JSONArray segments = itinerary.getJSONArray("segments");

                    JSONObject firstSegment = segments.getJSONObject(0);
                    JSONObject lastSegment = segments.getJSONObject(segments.length() - 1);

                    Map<String, String> flightData = new HashMap<>();
                    flightData.put("price", flight.getJSONObject("price").getString("total") + " EUR");
                    flightData.put("airline", firstSegment.getString("carrierCode"));
                    flightData.put("flightNumber", firstSegment.getString("number"));

                    flightData.put("departureTime", firstSegment.getJSONObject("departure").getString("at"));
                    flightData.put("departureAirport", firstSegment.getJSONObject("departure").getString("iataCode"));

                    flightData.put("arrivalTime", lastSegment.getJSONObject("arrival").getString("at"));
                    flightData.put("arrivalAirport", lastSegment.getJSONObject("arrival").getString("iataCode"));

                    if (segments.length() > 1) {
                        flightData.put("hasConnections", "true");
                        flightData.put("totalStops", String.valueOf(segments.length() - 1));

                        StringBuilder connections = new StringBuilder();
                        for (int j = 0; j < segments.length() - 1; j++) {
                            JSONObject segment = segments.getJSONObject(j);
                            connections.append(segment.getJSONObject("arrival").getString("iataCode"));
                            if (j < segments.length() - 2) {
                                connections.append(", ");
                            }
                        }
                        flightData.put("connectionAirports", connections.toString());
                    }

                    results.add(flightData);
                }
            } else {
                System.out.println("Amadeus API Error: Status " + response.getStatusCode() + ", Body: " + response.getBody());
            }
        } catch (Exception e) {
            System.out.println("Amadeus API Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
}