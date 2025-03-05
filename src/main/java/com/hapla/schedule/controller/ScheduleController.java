package com.hapla.schedule.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hapla.schedule.model.service.ScheduleService;
import com.hapla.schedule.model.vo.Detail;
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
	
	// 일정 페이지로 이동 / DB 저장
	@GetMapping("/schedule")
	public String Schedule(@ModelAttribute Trip trip, HttpSession session) {
		
		Users user = (Users)session.getAttribute("loginUser");
		
		// Trip 객체 생성
		trip.setUserNo(user.getUserNo());
		trip.setStartDate(trip.getStartDate());
		trip.setEndDate(trip.getEndDate());
		trip.setCityName(trip.getCityName());
		
		// System.out.println("trip : " + trip);
		
		// 서비스 호출하여 DB 저장
		scheduleService.saveTrip(trip);
		return "/schedule/schedule";
	}
	
	// 일정 목록 페이지로 이동
	@GetMapping("/scheduleList")
	public String ScheduleList(@ModelAttribute Trip trip, HttpSession session, Model model) {
		
		Users user = (Users)session.getAttribute("loginUser");
		
		List<Trip> schedules = scheduleService.getMySchedule(user.getUserNo());
		
		model.addAttribute("trip", schedules);
		return "/schedule/scheduleList";
	}
	
	// 일정 내용 페이지로 이동
	@GetMapping("/scheduleDetail")
	public String ScheduleDetail(@ModelAttribute Detail detail, HttpSession session, Model model) {
		
		Trip trip = (Trip)session.getAttribute("tripNo");
		
		List<Detail> tripDetail = scheduleService.getTripDetail(trip.getTripNo());
		
		model.addAttribute("detail", tripDetail);
		return "/schedule/scheduleDetail";
		
	}
	
}