package com.hapla.test.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class GoogleSearchController {

    @Value("${google.api.key}") // Google API Key를 application.properties에서 설정
    private String googleApiKey;

    @GetMapping("/search")
    public ResponseEntity<String> searchPlaces(@RequestParam String city, @RequestParam String category) {
        try {
            // 구글 Places API URL 설정 (language=ko 추가)
            String url = String.format("https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s+%s&language=ko&key=%s",
                    city, category, googleApiKey);

            // RestTemplate을 이용한 API 호출
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // 응답이 성공적이면 UTF-8로 응답을 반환
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new String(response.getBytes(), StandardCharsets.UTF_8));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\":\"ERROR\", \"message\":\"서버 오류\"}");
        }
    }



    // 응답 본문을 파싱하여 적절한 형식으로 반환
    private Object parsePlaces(String jsonResponse) {
        // JSON 파싱 로직 (예: Jackson 사용)
        // 예시로 받은 JSON 데이터를 적절히 파싱하여 결과를 반환
        return jsonResponse; // 이 부분을 실제 파싱 로직으로 변경하세요.
    }
}
