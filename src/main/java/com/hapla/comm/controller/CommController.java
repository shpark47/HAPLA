package com.hapla.comm.controller;

import com.hapla.comm.model.service.CommService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CommController {
    private final CommService commService;
}
