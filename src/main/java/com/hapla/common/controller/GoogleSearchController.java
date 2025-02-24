package com.hapla.common.controller;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class GoogleSearchController {

    @Value("${google.api.key}") // Google API Key를 application.properties에서 설정
    private String googleApiKey;

    @GetMapping("/search")
    public ResponseEntity<String> searchPlaces(@RequestParam("city") String city, @RequestParam("category") String category) {
        try {
            // 구글 Places API URL 설정 (language=ko 추가)
            String url = String.format("https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s+%s&language=ko&key=%s",
                    city, category, googleApiKey);

            System.out.println(url);

            // RestTemplate을 이용한 API 호출
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // 응답을 JSON으로 파싱하여 결과에서 최대 20개 항목만 필터링
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray results = jsonResponse.getJSONArray("results");

            // 20개까지만 자르기
            int maxResults = Math.min(results.length(), 20);
            JSONArray limitedResults = new JSONArray();
            for (int i = 0; i < maxResults; i++) {
                limitedResults.put(results.get(i));
            }

            // 제한된 20개 결과를 새로운 JSON 객체에 담기
            jsonResponse.put("results", limitedResults);

            // 응답이 성공적이면 UTF-8로 응답을 반환
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\":\"ERROR\", \"message\":\"서버 오류\"}");
        }
    }

}
