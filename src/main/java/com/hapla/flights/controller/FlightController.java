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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.hapla.flights.model.service.FlightService;
import com.hapla.flights.model.vo.AirlineInfo;
import com.hapla.flights.model.vo.Airport;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/flight")
public class FlightController {


    @Value("${AMADEUS.API.ID}")
    private String AMADEUS_API_ID;
    @Value("${AMADEUS.API.KEY}")
    private String AMADEUS_API_KEY;
    @Value("${TAGO.API.KEY}")
    private String TAGO_API_KEY;
    
    
    
    private final FlightService fService;
//    public List<Airport> loadCSV() {
//        List<Airport> airports = new ArrayList<>();
//        try (CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/static/csv/airports.csv"))) {
//            String[] values;
//            while ((values = csvReader.readNext()) != null) {
//                Airport airport = new Airport();
//                airport.setIataCode(values[0]);
//                airport.setEngAirportName(values[1]);
//                airport.setKorAirportName(values[2]);
//                airport.setEngCityName(values[3]);
//                airport.setKorCityName(values[4]);
//                airport.setKorCountryName(values[5]);
//                airport.setEngCountryName(values[6]);
//                airports.add(airport);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (CsvValidationException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return airports;
//    }
    
	  public List<AirlineInfo> loadCSV() {
	  List<AirlineInfo> airlineList = new ArrayList<>();
	  try (CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/static/csv/airlineNames.csv"))) {
	      String[] values;
	      while ((values = csvReader.readNext()) != null) {
	          AirlineInfo airline = new AirlineInfo();
	          airline.setKorAirline(values[1]);
	          airline.setCarrierCode(values[2]);
	        
	          airlineList.add(airline);
	      }
	  } catch (FileNotFoundException e) {
	      e.printStackTrace();
	  } catch (CsvValidationException e) {
	      e.printStackTrace();
	  } catch (IOException e) {
	      e.printStackTrace();
	  }
	  return airlineList;
	}
    
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/EUR";

    public double getExchangeRate() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            // API에서 데이터를 가져오기
            String response = restTemplate.getForObject(API_URL, String.class);
            JSONObject json = new JSONObject(response);
            JSONObject rates = json.getJSONObject("rates");
            return rates.getDouble("KRW");
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error while fetching exchange rates", e);
        }
    }
    

    @GetMapping(value = "/search", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<Airport> searchAirports(@RequestParam("query") String query) {
    	List<Airport> searchList = fService.searchList(query);
        return searchList;
    } 

    public List<Map<String, Object>> removeDuplicates(List<Map<String, Object>> flightOffers) {
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

    public boolean isDomesticFlight(String departureCode, String arrivalCode) {
        List<String> koreanAirports = Arrays.asList("ICN", "GMP", "PUS", "CJU", "TAE", "KWJ", "RSU", "KPO", "WJU",
                "USN", "HIN", "YNY");
        return koreanAirports.contains(departureCode) && koreanAirports.contains(arrivalCode);
    }

    public String getAirportId(String iataCode) {
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

    public String getAmadeusAccessToken() {
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
                System.out.println("json : " + json);
                return json.getString("access_token");
            }
        } catch (Exception e) {
            System.out.println("Amadeus Token Request Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getDomesticFlightOffers(String departure, String arrival, String departureDate, String returnDate, String travelers) {
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

    public String formatTAGODateTime(String tagoDateTime) {
        return tagoDateTime.substring(0, 4) + "-" + tagoDateTime.substring(4, 6) + "-" + tagoDateTime.substring(6, 8)
                + "T" + tagoDateTime.substring(8, 10) + ":" + tagoDateTime.substring(10, 12) + ":00";
    }
    
    @GetMapping("/flightSearch")
    public String flightSearch(@RequestParam("departureName") String departure,
            @RequestParam("arrivalName") String arrival, @RequestParam("dates") String dates,
            @RequestParam("travelers") String travelers, Model model){

        System.out.println("=== Flight Search Started ===");
        System.out.println("Input parameters:");
        System.out.println("Departure: " + departure);
        System.out.println("Arrival: " + arrival);
        System.out.println("Dates: " + dates);
        System.out.println("Travelers: " + travelers);
        
       
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
        HashMap<String, String> iataMap = new HashMap<String, String>();
        iataMap.put("departureCode", departureCode);
        iataMap.put("arrivalCode", arrivalCode);
        
        
        try {
            System.out.println("Extracted IATA codes - Departure: " + departureCode + ", Arrival: " + arrivalCode);

            String[] dateSplit = dates.split(" ~ ");
            String departureDate = dateSplit[0].trim();
            String returnDate = (dateSplit.length > 1) ? dateSplit[1].trim() : null;

            System.out.println("Parsed dates - Departure: " + departureDate + ", Return: " + returnDate);

            
         // 1. departureDate 파싱 및 유효성 검사 (returnDate는 기본값으로 강제로 설정하지 않음)
            try {
                LocalDate depDate = LocalDate.parse(departureDate);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid departure date format: " + departureDate);
                
                    model.addAttribute("error", "잘못된 날짜 형식입니다.");
                    return "flightSearchResult";
            }

            // 2. returnDate가 제공된 경우에만 파싱 및 유효성 검사
            if (returnDate != null && !returnDate.trim().isEmpty()) {
                try {
                    LocalDate depDate = LocalDate.parse(departureDate);
                    LocalDate retDate = LocalDate.parse(returnDate);
                    if (retDate.isBefore(depDate)) {
                        System.out.println("Return date is before departure date: " + returnDate + ", treating as one-way.");
                        returnDate = null; // 출발일보다 이전이면 편도로 처리
                    } else if (retDate.isEqual(depDate)) {
                        System.out.println("Return date equals departure date: " + returnDate + ", treating as one-way.");
                        returnDate = null; // 출발일과 같으면 편도로 처리 (필요에 따라 정책 변경 가능)
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid return date format: " + returnDate);
                    
                        model.addAttribute("error", "잘못된 반환 날짜 형식입니다.");
                        return "flightSearchResult";
                    
                }
            } else {
                System.out.println("No return date provided, treating as one-way search.");
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

            Set<String> uniqueFlightNumbers = new HashSet<>();
            List<Map<String, Object>> uniqueFlightOffers = new ArrayList<>();

            for (Map<String, Object> offer : flightOffers) {
                String flightNumber = (String) offer.get("flightNumber");
                if (!uniqueFlightNumbers.contains(flightNumber)) {
                    uniqueFlightNumbers.add(flightNumber);
                    uniqueFlightOffers.add(offer);
                }
            }


            System.out.println("<<<<<<<<<" + uniqueFlightOffers);
            System.out.println("Flight offers found: " + uniqueFlightOffers.size());
            if (!uniqueFlightOffers.isEmpty()) {
                System.out.println("First flight offer: " + uniqueFlightOffers.get(0));
                System.out.println("Outbound Airline: " + uniqueFlightOffers.get(0).get("outboundAirline"));
                System.out.println("Inbound Airline: " + uniqueFlightOffers.get(0).get("inboundAirline"));
            }
            	fService.countPlus(iataMap);
            	List<AirlineInfo> airlineList = loadCSV();
	            model.addAttribute("flightOffers", uniqueFlightOffers).addAttribute("airline", airlineList);
	            System.out.println("uniqueFlightOffers : " + uniqueFlightOffers);
	            return "flightSearchResult";
            
        } catch (Exception e) {
            System.out.println("Exception in flight search: " + e.getMessage());
            e.printStackTrace();
	            model.addAttribute("error", "항공권 검색 중 오류가 발생했습니다: " + e.getMessage());
	            return "flightSearchResult";
            
        }
    }
    

    public List<Map<String, Object>> getFlightOffers(String accessToken, String departure, String arrival,
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
        System.out.println("url : " + url);
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
                    double exchangeRate = getExchangeRate();
                    double totalPrice = Double.parseDouble(flight.getJSONObject("price").getString("total"));
                    int numberOfPassengers = Integer.parseInt(travelers);
                    double pricePerPerson = totalPrice / numberOfPassengers; // 1인 기준 가격 계산
                    int result = (int) Math.ceil(exchangeRate * pricePerPerson);
                    
                    flightData.put("price",  result + "원");

                    // carrierCode 추가
                    String outboundCarrierCode = outboundFirstSegment.getString("carrierCode");
                    String outboundAirline = carriersDict.optString(outboundCarrierCode, outboundCarrierCode);
                    String outboundKorAirlineName = null;
                    String inboundKorAirlineName = null;
                    List<AirlineInfo> airlineList = loadCSV();
                    for(AirlineInfo airline : airlineList) {
                    	if(airline.getCarrierCode().equals(outboundCarrierCode)) {
                    		outboundKorAirlineName = airline.getKorAirline();
                    	}
                    }
                    flightData.put("carrierCode", outboundCarrierCode);  // ✅ 추가
                    flightData.put("airline", outboundAirline);
                    flightData.put("outboundAirline", outboundAirline);
                    flightData.put("outboundCarrierCode", outboundCarrierCode); // ✅ 추가
                    flightData.put("inboundSegment", inboundSegments);
                    flightData.put("outboundKorAirlineName", outboundKorAirlineName);

                    if (inboundFirstSegment != null) {
                        String inboundCarrierCode = inboundFirstSegment.getString("carrierCode");
                        String inboundAirline = carriersDict.optString(inboundCarrierCode, inboundCarrierCode);
                        for(AirlineInfo airline : airlineList) {
                        	if(airline.getCarrierCode().equals(inboundCarrierCode)) {
                        		inboundKorAirlineName = airline.getKorAirline();
                        	}
                        }
                        flightData.put("inboundCarrierCode", inboundCarrierCode); // ✅ 추가
                        flightData.put("inboundAirline", inboundAirline);
                        flightData.put("inboundKorAirlineName", inboundKorAirlineName);
                    } else {
                        flightData.put("inboundAirline", outboundAirline);
                        flightData.put("inboundCarrierCode", outboundCarrierCode); // ✅ 추가
                    }

                    flightData.put("outboundDepartureTime", LocalDateTime.parse(outboundFirstSegment.getJSONObject("departure").getString("at")));
                    flightData.put("outboundDepartureAirport", outboundFirstSegment.getJSONObject("departure").getString("iataCode"));
                    flightData.put("outboundArrivalTime", LocalDateTime.parse(outboundLastSegment.getJSONObject("arrival").getString("at")));
                    flightData.put("outboundArrivalAirport", outboundLastSegment.getJSONObject("arrival").getString("iataCode"));
                    flightData.put("inboundSegment", inboundSegments);

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