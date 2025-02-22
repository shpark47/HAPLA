package com.hapla.flights.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hapla.flights.model.vo.Airport;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Controller
@RequestMapping("/flight")
public class FlightController {
	
	private List<Airport> airports = loadCSV();

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
	public void flightSearch(@RequestParam("departure") String departure,
	                         @RequestParam("arrival") String arrival,
	                         @RequestParam("dates") String dates,
	                         @RequestParam Map<String, String> travelers) {
	    System.out.println(departure + "," + arrival + "," + dates + "," + travelers);
	}

	
}