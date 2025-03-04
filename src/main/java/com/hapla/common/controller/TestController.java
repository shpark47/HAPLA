package com.hapla.common.controller;

import com.hapla.users.model.vo.Users;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Controller
public class TestController {
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
}
