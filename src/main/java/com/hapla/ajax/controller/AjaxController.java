package com.hapla.ajax.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.hapla.schedule.model.service.ScheduleService;
import com.hapla.schedule.model.vo.Detail;
import com.hapla.users.model.vo.Users;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@SessionAttributes("loginUser")
public class AjaxController {
	
	private final ScheduleService scheduleService;
	
	@PostMapping("/schedule/saveDetail")
	public String saveDetail(Model model, @RequestBody Map<Object, Object> detail) {
	    Users loginUser = (Users) model.getAttribute("loginUser");
		System.out.println("들어왔다.");
		System.out.println(loginUser);
		System.out.println(detail);
		return null;
	}
}
