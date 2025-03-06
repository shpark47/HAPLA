package com.hapla.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping("/recommend")
    public String recommend() {
        return "/recommend";
    }

    @GetMapping("/schedule")
    public String schedule() {
        return "/schedule";
    }
}
