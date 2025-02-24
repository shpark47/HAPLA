package com.hapla.users.controller;

import com.hapla.exception.Exception;
import com.hapla.users.model.service.UsersService;
import com.hapla.users.model.vo.Users;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequiredArgsConstructor
@SessionAttributes("loginUser")
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/checkUser")
    @ResponseBody
    public String checkUser(@RequestBody Users user, HttpSession session) {
        int checkResult = usersService.checkUser(user);
        Users u = usersService.login(user);
        JSONObject json = new JSONObject();
        json.put("checkResult", checkResult);
        json.put("user", u);

        session.setAttribute("loginUser", u);

        return json.toString();
    }

    @PostMapping("/join")
    @ResponseBody
    public String join(@RequestBody Users user) {
        System.out.println(user);
        int result = usersService.join(user);
        if (result == 1) {
            JSONObject json = new JSONObject();
            json.put("success", true);
            return json.toString();
        }
        throw new Exception("회원가입에 실패하였습니다.");
    }
}
