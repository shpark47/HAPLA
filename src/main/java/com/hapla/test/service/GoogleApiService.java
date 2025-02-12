package com.hapla.test.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;

@Service
public class GoogleApiService {

    @Value("${google.api.key}") // Google API Key를 application.properties에서 설정
    private String googleApiKey;

    public JsonObject searchPlaces(String city, String category) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Geocoding API 호출하여 위도/경도 가져오기
        String geoUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + city + "&key=" + googleApiKey;
        ResponseEntity<String> geoResponse = restTemplate.getForEntity(geoUrl, String.class);

        Gson gson = new Gson();
        JsonObject geoJson = gson.fromJson(geoResponse.getBody(), JsonObject.class);

        // 위도와 경도 추출
        double lat = geoJson.getAsJsonArray("results").get(0).getAsJsonObject()
                .getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsDouble();
        double lng = geoJson.getAsJsonArray("results").get(0).getAsJsonObject()
                .getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsDouble();

        // 2. Places API 호출하여 해당 카테고리의 장소 검색
        String placesUrl = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=5000&type=%s&key=%s",
                lat, lng, category, googleApiKey);

        ResponseEntity<String> placesResponse = restTemplate.getForEntity(placesUrl, String.class);

        return gson.fromJson(placesResponse.getBody(), JsonObject.class); // 결과 반환
    }
}
