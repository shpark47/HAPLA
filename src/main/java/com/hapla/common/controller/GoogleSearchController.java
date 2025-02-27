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

@RestController
@RequiredArgsConstructor
public class GoogleSearchController {

    @Value("${google.api.key}") // application.properties 또는 .env에서 Google API 키를 주입
    private String googleApiKey; // Google Places API 호출에 사용할 API 키

    @GetMapping("/search")
    public ResponseEntity<String> searchPlaces(
            @RequestParam("city") String city,
            @RequestParam("category") String category) {
        try {
            // 구글 Places API URL 설정 (language=ko 추가)
            String url = String.format("https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s+%s&language=ko&key=%s",
                    city, category, googleApiKey); // 도시와 카테고리를 쿼리로 사용해 URL 생성

            System.out.println(url); // 디버깅용으로 생성된 URL 출력

            // RestTemplate을 이용한 API 호출
            RestTemplate restTemplate = new RestTemplate(); // HTTP 요청을 보내기 위한 RestTemplate 객체 생성
            String response = restTemplate.getForObject(url, String.class); // API 호출 후 응답을 문자열로 받음

            // 응답을 JSON으로 파싱하여 결과에서 최대 20개 항목만 필터링
            JSONObject jsonResponse = new JSONObject(response); // 응답 문자열을 JSON 객체로 변환
            JSONArray results = jsonResponse.getJSONArray("results"); // "results" 배열 추출

            // 20개까지만 자르기
            int maxResults = Math.min(results.length(), 20); // 결과 개수를 최대 20개로 제한
            JSONArray limitedResults = new JSONArray(); // 제한된 결과를 담을 새로운 배열 생성
            for (int i = 0; i < maxResults; i++) {
                limitedResults.put(results.get(i)); // 최대 20개 항목을 새 배열에 추가
            }

            // 제한된 20개 결과를 새로운 JSON 객체에 담기
            jsonResponse.put("results", limitedResults); // 원래 JSON 객체의 "results"를 갱신

            // 응답이 성공적이면 UTF-8로 응답을 반환
            return ResponseEntity.ok() // HTTP 200 상태로 응답 객체 생성
                    .contentType(MediaType.APPLICATION_JSON) // 응답 타입을 JSON으로 설정
                    .body(jsonResponse.toString()); // JSON 문자열로 응답 본문 설정

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\":\"ERROR\", \"message\":\"서버 오류\"}");
        }
    }
}