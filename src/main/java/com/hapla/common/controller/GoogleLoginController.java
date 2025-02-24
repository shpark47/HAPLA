package com.hapla.common.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

@RestController
@RequestMapping("/google-login")
public class GoogleLoginController {

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyGoogleToken(@RequestBody Map<String, String> requestBody) {
        String token = requestBody.get("token");
        try {
            // 구글의 공개 키를 사용해 JWT 검증
            String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + token;
            URL obj = new URL(url);
            InputStreamReader reader = new InputStreamReader(obj.openStream());
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            String userId = jsonObject.get("sub").getAsString();
            String email = jsonObject.get("email").getAsString();
            String name = jsonObject.get("name").getAsString();
            String pictureUrl = jsonObject.get("picture").getAsString();

            // 반환할 사용자 정보
            JsonObject response = new JsonObject();
            response.addProperty("userId", userId);
            response.addProperty("email", email);
            response.addProperty("name", name);
            response.addProperty("picture", pictureUrl);
            System.out.println(response);

            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to verify token");
        }
    }
}