package com.hapla.flights.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

	

	@GetMapping(value="/search",  produces = "application/json;charset=UTF-8")
	@ResponseBody
	public List<Airport> searchAirports(@RequestParam("query") String query) {
	    return airports.stream()
	            .filter(airport -> airport.getAirportsKoName().toLowerCase().contains(query.toLowerCase())) // 이름 필터링
	            .limit(5) // 최대 5개 결과 반환
	            .collect(Collectors.toList()); // 전체 Airport 객체 반환
	}
	
	@GetMapping("/flightSearch")
	public String flightSearch(@RequestParam("departureName") String departure,
	                           @RequestParam("arrivalName") String arrival,
	                           @RequestParam("dates") String dates,
	                           @RequestParam("travelers") String travelers,
	                           Model model) {

	    // ✨ IATA 코드만 추출하는 정규식
	    String iataPattern = "\\((\\w{3})\\)";
	    Pattern pattern = Pattern.compile(iataPattern);

	    Matcher departureMatcher = pattern.matcher(departure);
	    Matcher arrivalMatcher = pattern.matcher(arrival);

	    if (!departureMatcher.find() || !arrivalMatcher.find()) {
	        model.addAttribute("error", "출발지와 도착지에서 IATA 코드를 찾을 수 없습니다.");
	        return "flightSearchResult";
	    }

	    String departureCode = departureMatcher.group(1); // ICN
	    String arrivalCode = arrivalMatcher.group(1); // ELS

	    // ✨ 날짜 포맷 변환 (YYYY-MM-DD 형식 유지)
	    String[] dateSplit = dates.split(" ~ ");
	    String departureDate = dateSplit[0]; // 2025-02-22
	    String returnDate = (dateSplit.length > 1) ? dateSplit[1] : null; // 2025-03-01 or null

	    // ✨ Amadeus API 요청 처리
	    String accessToken = getAmadeusAccessToken();
	    if (accessToken == null) {
	        model.addAttribute("error", "Amadeus API 인증 실패");
	        return "flightSearchResult";
	    }

	    List<Map<String, String>> flightOffers = getFlightOffers(accessToken, departureCode, arrivalCode, departureDate, returnDate, travelers);
	    model.addAttribute("flightOffers", flightOffers);

	    return "flightSearchResult";
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
            e.printStackTrace();
        }
        return null;
    }
    
    private List<Map<String, String>> getFlightOffers(String accessToken, String departure, String arrival, String departureDate, String returnDate, String travelers) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://test.api.amadeus.com/v2/shopping/flight-offers"
                   + "?originLocationCode=" + departure
                   + "&destinationLocationCode=" + arrival
                   + "&departureDate=" + departureDate
                   + "&returnDate=" + returnDate
                   + "&adults=" + travelers
                   + "&max=10";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        List<Map<String, String>> results = new ArrayList<>();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                JSONArray flights = json.getJSONArray("data");

                for (int i = 0; i < flights.length(); i++) {
                    JSONObject flight = flights.getJSONObject(i);
                    JSONObject itinerary = flight.getJSONArray("itineraries").getJSONObject(0);
                    JSONArray segments = itinerary.getJSONArray("segments");
                    
                    // Get first segment for departure info
                    JSONObject firstSegment = segments.getJSONObject(0);
                    // Get last segment for arrival info
                    JSONObject lastSegment = segments.getJSONObject(segments.length() - 1);

                    Map<String, String> flightData = new HashMap<>();
                    flightData.put("price", flight.getJSONObject("price").getString("total") + " EUR");
                    flightData.put("airline", firstSegment.getString("carrierCode"));
                    flightData.put("flightNumber", firstSegment.getString("number"));
                    
                    // Use first segment's departure
                    flightData.put("departureTime", firstSegment.getJSONObject("departure").getString("at"));
                    flightData.put("departureAirport", firstSegment.getJSONObject("departure").getString("iataCode"));
                    
                    // Use last segment's arrival
                    flightData.put("arrivalTime", lastSegment.getJSONObject("arrival").getString("at"));
                    flightData.put("arrivalAirport", lastSegment.getJSONObject("arrival").getString("iataCode"));

                    // Add connection information
                    if (segments.length() > 1) {
                        flightData.put("hasConnections", "true");
                        flightData.put("totalStops", String.valueOf(segments.length() - 1));
                        
                        // Add connection details
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}