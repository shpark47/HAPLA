package com.hapla.users.controller;

import com.hapla.users.model.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;
}
