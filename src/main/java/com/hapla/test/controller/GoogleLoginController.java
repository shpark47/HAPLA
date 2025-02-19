package com.hapla.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class GoogleLoginController {

    @GetMapping("/google-login/verify-token")
    public String verifyGoogleToken(@RequestParam("token") String token) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + token;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        System.out.println(response.getBody());

        // Google의 토큰 정보가 포함된 JSON 응답을 파싱 후 반환
        return response.getBody();
    }
}
