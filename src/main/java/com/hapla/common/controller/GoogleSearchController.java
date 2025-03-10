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

import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/searchAll")
    public ResponseEntity<String> searchAllPlaces(@RequestParam("city") String city) {
        try {
            // 응답을 담을 객체 생성
            JSONObject filteredResponse = new JSONObject();
            filteredResponse.put("status", "OK");

            // 카테고리별 쿼리
            String[] categories = {"tourist_attraction", "landmark", "lodging", "restaurant"};
            String[] queryPrefixes = {" 관광지", " 랜드마크", " 호텔", " 레스토랑"};
            HashMap<String, JSONArray> categoryResults = new HashMap<>();

            RestTemplate restTemplate = new RestTemplate();

            // 1️⃣ 도시 이름을 영어로 변환하는 API 호출
            String enCity = getCityInEnglish(city, restTemplate);

            // 2️⃣ main_info 가져오기 (기존 쿼리)
            String mainUrl = String.format("https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&language=ko&key=%s",
                    city, googleApiKey);  // 한글로 검색
            String mainResponse = restTemplate.getForObject(mainUrl, String.class);
            JSONObject mainJsonResponse = new JSONObject(mainResponse);
            JSONArray mainResults = mainJsonResponse.getJSONArray("results");

            if (mainResults.length() > 0) {
                JSONObject mainPlace = mainResults.getJSONObject(0);
                String photoUrl = "";
                if (mainPlace.has("photos")) {
                    JSONArray photos = mainPlace.getJSONArray("photos");
                    if (photos.length() > 0) {
                        String photoReference = photos.getJSONObject(0).getString("photo_reference");
                        photoUrl = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=%s&key=%s",
                                photoReference, googleApiKey);
                    }
                }
                JSONObject mainInfo = new JSONObject();
                mainInfo.put("name", mainPlace.getString("name"));
                mainInfo.put("photo_url", photoUrl);
                mainInfo.put("description", mainPlace.has("formatted_address") ? mainPlace.getString("formatted_address") : "설명 없음");
                mainInfo.put("place_id", mainPlace.getString("place_id"));
                mainInfo.put("review_count", mainPlace.has("user_ratings_total") ? mainPlace.getInt("user_ratings_total") : 0);
                mainInfo.put("enCity", enCity);
                filteredResponse.put("main_info", mainInfo);
            }

            // 카테고리별 결과 초기화
            for (String category : categories) {
                categoryResults.put(category, new JSONArray());
            }

            // 3️⃣ 카테고리별 API 호출 최적화
            for (int i = 0; i < categories.length; i++) {
                String categoryQuery = enCity + queryPrefixes[i];  // 영어로 된 도시 이름 사용
                String url = String.format("https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&language=ko&key=%s",
                        categoryQuery, googleApiKey);
                String response = restTemplate.getForObject(url, String.class);
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray results = jsonResponse.getJSONArray("results");

                for (int j = 0; j < results.length() && categoryResults.get(categories[i]).length() < 4; j++) {
                    JSONObject place = results.getJSONObject(j);
                    JSONObject simplifiedPlace = new JSONObject();
                    simplifiedPlace.put("name", place.getString("name"));
                    simplifiedPlace.put("rating", place.has("rating") ? place.getDouble("rating") : 0);
                    simplifiedPlace.put("address", place.has("formatted_address") ? place.getString("formatted_address") : "");
                    String placePhotoUrl = "";
                    if (place.has("photos")) {
                        JSONArray photos = place.getJSONArray("photos");
                        if (photos.length() > 0) {
                            String photoReference = photos.getJSONObject(0).getString("photo_reference");
                            placePhotoUrl = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=%s&key=%s",
                                    photoReference, googleApiKey);
                        }
                    }
                    simplifiedPlace.put("photo_url", placePhotoUrl);
                    simplifiedPlace.put("place_id", place.getString("place_id"));
                    simplifiedPlace.put("review_count", place.has("user_ratings_total") ? place.getInt("user_ratings_total") : 0);
                    categoryResults.get(categories[i]).put(simplifiedPlace);
                }
            }

            // 카테고리별 결과 추가
            for (String category : categories) {
                filteredResponse.put(category, categoryResults.get(category));
            }

            // 최종적으로 필터링된 JSON 응답 반환
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(filteredResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\":\"ERROR\", \"message\":\"서버 오류\"}");
        }
    }

    /**
     * Google Places API를 사용하여 도시 이름을 영어로 변환하는 메서드
     */
    private String getCityInEnglish(String city, RestTemplate restTemplate) {
        try {
            // 영어로 도시 이름을 검색하여 변환
            String urlEn = String.format(
                    "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%s&inputtype=textquery&language=ko&key=%s",  // 검색 언어를 한국어로 설정
                    city, googleApiKey);

            String responseEn = restTemplate.getForObject(urlEn, String.class);
            JSONObject jsonResponseEn = new JSONObject(responseEn);

            if (jsonResponseEn.has("candidates") && jsonResponseEn.getJSONArray("candidates").length() > 0) {
                // place_id 가져오기
                String placeId = jsonResponseEn.getJSONArray("candidates").getJSONObject(0).getString("place_id");

                // place details API를 통해 영어 이름 받기
                String detailsUrl = String.format(
                        "https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&language=en&key=%s", placeId, googleApiKey);
                String detailsResponse = restTemplate.getForObject(detailsUrl, String.class);
                JSONObject detailsJsonResponse = new JSONObject(detailsResponse);

                if (detailsJsonResponse.has("result")) {
                    return detailsJsonResponse.getJSONObject("result").getString("name");  // 영어 이름 반환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return city; // 오류 발생 시 원래 입력값 반환
    }

}