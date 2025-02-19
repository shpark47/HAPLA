package com.hapla.test.controller;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
public class GoogleLoginController {

    // 로그인 후 구글 사용자 정보 받기
    @GetMapping("/google-login/verify-token")
    public String googleLoginVerifyToken(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User != null) {
            String userName = oauth2User.getName();  // 구글 프로필 이름
            String userEmail = oauth2User.getAttribute("email");  // 구글 이메일
            String userImage = oauth2User.getAttribute("picture");  // 구글 프로필 사진 URL

            // 사용자 정보를 처리하는 로직 추가 (DB에 저장, 세션에 저장 등)
            return String.format("Google User Info: Name = %s, Email = %s, Image = %s", userName, userEmail, userImage);
        }

        return "User information is not available";
    }

    // OAuth2 인증 이후 redirect URI
    @GetMapping("/login/oauth2/code/google")
    public String googleLoginRedirect(@RequestParam("code") String code) {
        // code로 서버와의 교환 후 사용자 정보 처리 로직을 추가할 수 있습니다.
        return "Google Login Success, Authorization Code: " + code;
    }
}
