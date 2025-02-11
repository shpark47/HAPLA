package com.hapla.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
