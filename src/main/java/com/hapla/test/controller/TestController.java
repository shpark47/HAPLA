package com.hapla.test.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Controller
public class TestController {
    @GetMapping("/main")
    public String main() {
        return "/main";
    }

    @GetMapping("/recommend")
    public String recommend() {
        return "/recommend";
    }

    @GetMapping("/attraction")
    public String attraction() {
        return "/attraction";
    }

    @GetMapping("/restaurant")
    public String restaurant() {
        return "/restaurant";
    }

    @GetMapping("/accommodation")
    public String accommodation() {
        return "/accommodation";
    }

    @GetMapping("/schedule")
    public String schedule() {
        return "/schedule";
    }

    @GetMapping("/google")
    public String google() {
        return "/google";
    }



    @Value("${google.api.key}") // Google API Key를 application.properties에서 설정
    private String googleApiKey;

    @GetMapping("/detail/{placeId}")
    public String getPlaceDetails(@PathVariable("placeId") String placeId, Model model) {
        System.out.println("placeId: " + placeId);
        String url = String.format("https://maps.googleapis.com/maps/api/place/details/json?placeId=%s&key=%s", placeId, googleApiKey);
        System.out.println("Request URL: " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);  // API 요청

        Map<String, Object> placeData = response.getBody();  // 응답에서 데이터 추출
        System.out.println("API Response: " + placeData);  // 응답 내용 출력

        if (placeData != null) {
            String status = (String) placeData.get("status");
            if ("OK".equals(status)) {
                // 성공적으로 데이터를 받았을 경우
                Map<String, Object> placeDetails = (Map<String, Object>) placeData.get("result");

                if (placeDetails != null) {
                    model.addAttribute("name", placeDetails.get("name"));
                    model.addAttribute("address", placeDetails.get("formatted_address"));
                    model.addAttribute("rating", placeDetails.get("rating"));

                    // 'photos'가 존재하는 경우
                    Object photosObj = placeDetails.get("photos");
                    if (photosObj != null) {
                        List<Map<String, Object>> photos = (List<Map<String, Object>>) photosObj;
                        if (!photos.isEmpty()) {
                            String photoReference = (String) photos.get(0).get("photo_reference");
                            model.addAttribute("photo", photoReference);
                        }
                    }

                    return "/place-detail";  // Thymeleaf 템플릿 이름
                } else {
                    model.addAttribute("error", "장소 정보가 없습니다.");
                    return "error";  // 장소 정보가 없을 경우
                }
            } else {
                String errorMessage = (String) placeData.get("error_message");
                model.addAttribute("error", "API 오류: " + errorMessage);
                return "error";  // API 오류일 경우
            }
        } else {
            model.addAttribute("error", "장소 정보를 불러올 수 없습니다.");
            return "error";  // 응답이 없을 경우
        }
    }


}
