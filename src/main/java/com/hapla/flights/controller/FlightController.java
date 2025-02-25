package com.hapla.flights.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
                .filter(airport -> airport.getAirportsKoName().toLowerCase().contains(query.toLowerCase()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @GetMapping("/flightSearch")
    public String flightSearch(@RequestParam("departureName") String departure,
            @RequestParam("arrivalName") String arrival, @RequestParam("dates") String dates,
            @RequestParam("travelers") String travelers, Model model) {

        System.out.println("=== Flight Search Started ===");
        System.out.println("Input parameters:");
        System.out.println("Departure: " + departure);
        System.out.println("Arrival: " + arrival);
        System.out.println("Dates: " + dates);
        System.out.println("Travelers: " + travelers);

        try {
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

            String[] dateSplit = dates.split(" ~ ");
            String departureDate = dateSplit[0].trim();
            String returnDate = (dateSplit.length > 1) ? dateSplit[1].trim() : null;

            System.out.println("Parsed dates - Departure: " + departureDate + ", Return: " + returnDate);

            if (returnDate == null || returnDate.isEmpty()) {
                try {
                    LocalDate depDate = LocalDate.parse(departureDate);
                    returnDate = depDate.plusDays(7).toString();
                    System.out.println("Return date not provided or empty, defaulting to: " + returnDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid departure date format: " + departureDate);
                    model.addAttribute("error", "잘못된 날짜 형식입니다.");
                    return "flightSearchResult";
                }
            } else {
                try {
                    LocalDate depDate = LocalDate.parse(departureDate);
                    LocalDate retDate = LocalDate.parse(returnDate);
                    if (retDate.isBefore(depDate) || retDate.isEqual(depDate)) {
                        returnDate = depDate.plusDays(7).toString();
                        System.out.println("Invalid return date, defaulting to: " + returnDate);
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid return date format: " + returnDate);
                    model.addAttribute("error", "잘못된 반환 날짜 형식입니다.");
                    return "flightSearchResult";
                }
            }

            boolean isDomestic = isDomesticFlight(departureCode, arrivalCode);
            System.out.println("Is domestic flight: " + isDomestic);

            System.out.println("API Keys present:");
            System.out.println("TAGO_API_KEY: " + TAGO_API_KEY);
            System.out.println("AMADEUS_API_ID: " + AMADEUS_API_ID + (AMADEUS_API_ID != null && !AMADEUS_API_ID.isEmpty()));
            System.out.println("AMADEUS_API_KEY: " + AMADEUS_API_KEY + (AMADEUS_API_KEY != null && !AMADEUS_API_KEY.isEmpty()));

            List<Map<String, Object>> flightOffers;
            if (isDomestic) {
                flightOffers = getDomesticFlightOffers(departureCode, arrivalCode, departureDate, returnDate, travelers);
            } else {
                String accessToken = getAmadeusAccessToken();
                if (accessToken == null) {
                    System.out.println("Failed to get Amadeus access token");
                    model.addAttribute("error", "국제선 항공권 검색 실패: 인증 오류");
                    return "flightSearchResult";
                }
                flightOffers = getFlightOffers(accessToken, departureCode, arrivalCode, departureDate, returnDate, travelers);
            }

            if (flightOffers == null || flightOffers.isEmpty()) {
                System.out.println("No flight offers retrieved.");
                model.addAttribute("error", "항공편을 찾을 수 없습니다.");
                return "flightSearchResult";
            }

            List<String> uniqueAirlines = flightOffers.stream()
                    .map(offer -> (String) offer.get("airline"))
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            List<Map<String, Object>> uniqueFlightOffers = new ArrayList<>();
            for (String airline : uniqueAirlines) {
                Map<String, Object> firstOffer = flightOffers.stream()
                        .filter(offer -> airline.equals(offer.get("airline")))
                        .findFirst()
                        .orElse(null);
                if (firstOffer != null) {
                    uniqueFlightOffers.add(firstOffer);
                }
            }

            System.out.println("<<<<<<<<<" + uniqueFlightOffers);
            System.out.println("Flight offers found: " + uniqueFlightOffers.size());
            if (!uniqueFlightOffers.isEmpty()) {
                System.out.println("First flight offer: " + uniqueFlightOffers.get(0));
                System.out.println("Outbound Airline: " + uniqueFlightOffers.get(0).get("outboundAirline"));
                System.out.println("Inbound Airline: " + uniqueFlightOffers.get(0).get("inboundAirline"));
            }

            model.addAttribute("flightOffers", uniqueFlightOffers);
            return "flightSearchResult";

        } catch (Exception e) {
            System.out.println("Exception in flight search: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "항공권 검색 중 오류가 발생했습니다: " + e.getMessage());
            return "flightSearchResult";
        }
    }

    private List<Map<String, Object>> removeDuplicates(List<Map<String, Object>> flightOffers) {
        if (flightOffers == null || flightOffers.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> uniqueOffers = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Map<String, Object> offer : flightOffers) {
            String key = (String) offer.get("flightNumber") + "_" + offer.get("departureTime");
            if (!seen.contains(key)) {
                seen.add(key);
                uniqueOffers.add(offer);
            }
        }

        return uniqueOffers;
    }

    private boolean isDomesticFlight(String departureCode, String arrivalCode) {
        List<String> koreanAirports = Arrays.asList("ICN", "GMP", "PUS", "CJU", "TAE", "KWJ", "RSU", "KPO", "WJU",
                "USN", "HIN", "YNY");
        return koreanAirports.contains(departureCode) && koreanAirports.contains(arrivalCode);
    }

    private String getAirportId(String iataCode) {
        Map<String, String> airportIdMap = new HashMap<>();
        airportIdMap.put("ICN", "NAARKSI");
        airportIdMap.put("GMP", "NAARKSS");
        airportIdMap.put("PUS", "NAARKPK");
        airportIdMap.put("CJU", "NAARKPC");
        airportIdMap.put("TAE", "NAARKTN");
        airportIdMap.put("KWJ", "NAARKJJ");
        airportIdMap.put("RSU", "NAARKJY");
        airportIdMap.put("KPO", "NAARKTH");
        airportIdMap.put("WJU", "NAARKNW");
        airportIdMap.put("USN", "NAARKPU");
        airportIdMap.put("HIN", "NAARKPS");
        airportIdMap.put("YNY", "NAARKNY");

        return airportIdMap.getOrDefault(iataCode, "UNKNOWN");
    }

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

    private List<Map<String, Object>> getDomesticFlightOffers(String departure, String arrival, String departureDate, String returnDate, String travelers) {
        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> results = new ArrayList<>();

        try {
            String url = "http://apis.data.go.kr/1613000/DmstcFlightNvgInfoService/getFlightOpratInfoList"
                    + "?serviceKey=" + TAGO_API_KEY
                    + "&pageNo=1"
                    + "&numOfRows=10"
                    + "&_type=json"
                    + "&depAirportId=" + URLEncoder.encode(getAirportId(departure), "UTF-8")
                    + "&arrAirportId=" + URLEncoder.encode(getAirportId(arrival), "UTF-8")
                    + "&depPlandTime=" + URLEncoder.encode(departureDate.replace("-", ""), "UTF-8");

            System.out.println("Domestic Flight API URL: " + url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println("Domestic API Response: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                JSONObject responseObj = json.getJSONObject("response");
                JSONObject bodyObj = responseObj.getJSONObject("body");
                JSONObject itemsObj = bodyObj.getJSONObject("items");

                JSONArray items;
                if (itemsObj.has("item") && itemsObj.get("item") instanceof JSONObject) {
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
                    Map<String, Object> flightData = new HashMap<>();

                    double priceKRW = flight.has("economyCharge") ? flight.getDouble("economyCharge") : 0.0;
                    double priceEUR = priceKRW / 1300.0;

                    String airline = flight.getString("airlineNm");
                    flightData.put("price", priceKRW > 0 ? String.format("%.2f EUR (₩%,.0f)", priceEUR, priceKRW) : "가격 정보 없음");
                    flightData.put("airline", airline);
                    flightData.put("outboundAirline", airline);
                    flightData.put("inboundAirline", airline);
                    flightData.put("flightNumber", flight.getString("vihicleId"));
                    flightData.put("departureTime", LocalDateTime.parse(formatTAGODateTime(String.valueOf(flight.getLong("depPlandTime")))));
                    flightData.put("arrivalTime", LocalDateTime.parse(formatTAGODateTime(String.valueOf(flight.getLong("arrPlandTime")))));
                    flightData.put("departureAirport", departure);
                    flightData.put("arrivalAirport", arrival);
                    flightData.put("isDomestic", "true");

                    System.out.println("Domestic flight data: " + flightData); // 디버깅 로그 추가
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

    private String formatTAGODateTime(String tagoDateTime) {
        return tagoDateTime.substring(0, 4) + "-" + tagoDateTime.substring(4, 6) + "-" + tagoDateTime.substring(6, 8)
                + "T" + tagoDateTime.substring(8, 10) + ":" + tagoDateTime.substring(10, 12) + ":00";
    }

    private List<Map<String, Object>> getFlightOffers(String accessToken, String departure, String arrival,
            String departureDate, String returnDate, String travelers) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://test.api.amadeus.com/v2/shopping/flight-offers"
                + "?originLocationCode=" + departure
                + "&destinationLocationCode=" + arrival
                + "&departureDate=" + departureDate
                + (returnDate != null ? "&returnDate=" + returnDate : "")
                + "&adults=" + travelers
                + "&max=200";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        List<Map<String, Object>> results = new ArrayList<>();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            System.out.println("Amadeus API URL: " + url);
            System.out.println("Amadeus API Response: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                JSONArray flights = json.getJSONArray("data");
                JSONObject dictionaries = json.optJSONObject("dictionaries");
                JSONObject carriersDict = (dictionaries != null) ? dictionaries.optJSONObject("carriers") : new JSONObject();

                System.out.println("Number of flights returned: " + flights.length());

                for (int i = 0; i < flights.length(); i++) {
                    JSONObject flight = flights.getJSONObject(i);
                    JSONArray itineraries = flight.getJSONArray("itineraries");

                    JSONObject outboundItinerary = itineraries.getJSONObject(0);
                    JSONArray outboundSegments = outboundItinerary.getJSONArray("segments");
                    JSONObject outboundFirstSegment = outboundSegments.getJSONObject(0);
                    JSONObject outboundLastSegment = outboundSegments.getJSONObject(outboundSegments.length() - 1);

                    JSONObject inboundItinerary = itineraries.optJSONObject(1);
                    JSONArray inboundSegments = null;
                    JSONObject inboundFirstSegment = null;
                    JSONObject inboundLastSegment = null;
                    if (inboundItinerary != null) {
                        inboundSegments = inboundItinerary.getJSONArray("segments");
                        inboundFirstSegment = inboundSegments.getJSONObject(0);
                        inboundLastSegment = inboundSegments.getJSONObject(inboundSegments.length() - 1);
                    }

                    Map<String, Object> flightData = new HashMap<>();
                    flightData.put("price", flight.getJSONObject("price").getString("total") + " EUR");

                    String outboundCarrierCode = outboundFirstSegment.getString("carrierCode");
                    String outboundAirline = carriersDict.optString(outboundCarrierCode, outboundCarrierCode);

                    flightData.put("airline", outboundAirline);
                    flightData.put("outboundAirline", outboundAirline);

                    if (inboundFirstSegment != null) {
                        String inboundCarrierCode = inboundFirstSegment.getString("carrierCode");
                        String inboundAirline = carriersDict.optString(inboundCarrierCode, inboundCarrierCode);
                        flightData.put("inboundAirline", inboundAirline);
                    } else {
                        flightData.put("inboundAirline", outboundAirline);
                    }

                    flightData.put("outboundDepartureTime", LocalDateTime.parse(outboundFirstSegment.getJSONObject("departure").getString("at")));
                    flightData.put("outboundDepartureAirport", outboundFirstSegment.getJSONObject("departure").getString("iataCode"));
                    flightData.put("outboundArrivalTime", LocalDateTime.parse(outboundLastSegment.getJSONObject("arrival").getString("at")));
                    flightData.put("outboundArrivalAirport", outboundLastSegment.getJSONObject("arrival").getString("iataCode"));

                    if (inboundFirstSegment != null && inboundLastSegment != null) {
                        flightData.put("inboundDepartureTime", LocalDateTime.parse(inboundFirstSegment.getJSONObject("departure").getString("at")));
                        flightData.put("inboundDepartureAirport", inboundFirstSegment.getJSONObject("departure").getString("iataCode"));
                        flightData.put("inboundArrivalTime", LocalDateTime.parse(inboundLastSegment.getJSONObject("arrival").getString("at")));
                        flightData.put("inboundArrivalAirport", inboundLastSegment.getJSONObject("arrival").getString("iataCode"));
                    }

                    if (outboundSegments.length() > 1) {
                        flightData.put("outboundHasConnections", "true");
                        flightData.put("outboundTotalStops", String.valueOf(outboundSegments.length() - 1));

                        StringBuilder outboundConnections = new StringBuilder();
                        for (int j = 0; j < outboundSegments.length() - 1; j++) {
                            JSONObject segment = outboundSegments.getJSONObject(j);
                            outboundConnections.append(segment.getJSONObject("arrival").getString("iataCode"));
                            if (j < outboundSegments.length() - 2) {
                                outboundConnections.append(", ");
                            }
                        }
                        flightData.put("outboundConnectionAirports", outboundConnections.toString());
                    } else {
                        flightData.put("outboundHasConnections", "false");
                        flightData.put("outboundTotalStops", "0");
                    }

                    if (inboundSegments != null && inboundSegments.length() > 1) {
                        flightData.put("inboundHasConnections", "true");
                        flightData.put("inboundTotalStops", String.valueOf(inboundSegments.length() - 1));

                        StringBuilder inboundConnections = new StringBuilder();
                        for (int j = 0; j < inboundSegments.length() - 1; j++) {
                            JSONObject segment = inboundSegments.getJSONObject(j);
                            inboundConnections.append(segment.getJSONObject("arrival").getString("iataCode"));
                            if (j < inboundSegments.length() - 2) {
                                inboundConnections.append(", ");
                            }
                        }
                        flightData.put("inboundConnectionAirports", inboundConnections.toString());
                    } else if (inboundSegments != null) {
                        flightData.put("inboundHasConnections", "false");
                        flightData.put("inboundTotalStops", "0");
                    }

                    flightData.put("flightNumber", outboundFirstSegment.getString("number"));


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