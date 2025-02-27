package com.hapla.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hapla.schedule.model.service.ScheduleService;
import com.hapla.schedule.model.vo.Trip;
import com.hapla.users.model.vo.Users;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
	
	private final ScheduleService scheduleService;
	/*
	 * // 생성자 Autowired 주입 
	 * public ScheduleController(ScheduleService scheduleService) { 
	 * this.scheduleService = scheduleService; }
	 */
	
	// 캘린더 페이지로 이동
	@GetMapping("/scheduleCalendar")
	public String ScheduleCalendar() {
		return "/schedule/scheduleCalendar";
	}
	
	// 일정 페이지로 이동
	@GetMapping("/schedule")
	public String Schedule(@ModelAttribute Trip trip, HttpSession session) {
		
		Users user = (Users)session.getAttribute("loginUser");
		trip.setUserNo(user.getUserNo());
		
		// 서비스 호출하여 DB 저장
//		scheduleService.saveTrip(trip);
		System.out.println("trip : " + trip);
		return "/schedule/schedule";
	}

}