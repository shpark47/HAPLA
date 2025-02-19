package com.hapla.flights.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hapla.flights.model.vo.Airport;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@RequestMapping("/flight")
public class FlightController {
	
	private List<Airport> airports = loadCSV();

	public List<Airport> loadCSV() {
		List<Airport> airports = new ArrayList<>();
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader("static/csv/airports.csv"));
			String[] values;
			while((values = csvReader.readNext()) != null) {
				Airport airport = new Airport();
				airport.setIataCode(values[0]);
				airport.setLocalName(values[1]);
				airport.setLatitude(values[2]);
				airport.setLongitude(values[3]);
				airport.setCityCode(values[4]);
				
				airports.add(airport);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CsvValidationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return airports;
	}
	
	@GetMapping("/search")
	@ResponseBody
	public List<String> searchAirports(@RequestParam("query") String query) {
	    return airports.stream()  // List<Airport>에서 stream() 호출
	            .filter(airport -> airport.getLocalName().toLowerCase().contains(query.toLowerCase())) // 이름 필터링
	            .limit(5) // 최대 5개 결과 반환
	            .map(Airport::getLocalName) // 공항 이름만 추출
	            .collect(Collectors.toList()); // 결과를 List<String>으로 수집
	}

}