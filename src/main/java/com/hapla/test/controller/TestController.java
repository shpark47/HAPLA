package com.hapla.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping("/main")
    public String main() {
        return "/main";
    }

    @GetMapping("/join")
    public String join() {
        return "/join";
    }
}
