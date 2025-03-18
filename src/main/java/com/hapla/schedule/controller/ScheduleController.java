package com.hapla.schedule.controller;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	@GetMapping("/calendar")
	public String ScheduleCalendar() {
		return "/schedule/calendar";
	}
	
	// 일정 페이지로 이동 / DB 저장
	@GetMapping("/schedule")
	public String Schedule(@ModelAttribute Trip trip, HttpSession session, Model model) {
		
		Users user = (Users)session.getAttribute("loginUser");
		
		// Trip 객체 생성
		trip.setUserNo(user.getUserNo());
		
//		System.out.println("trip : " + trip);
		
		// 1. 서비스 호출하여 DB 저장
		scheduleService.saveTrip(trip);
		Trip reTrip = scheduleService.selectOneTrip(trip);
		trip.setTripNo(reTrip.getTripNo());
		trip.setCreateDate(reTrip.getCreateDate());
		
		// 날짜 범위 생성
		List<Date> dateRange = scheduleService.getDateRange(trip.getStartDate(), trip.getEndDate());
		model.addAttribute("dateRange", dateRange);	// 날짜 범위 추가
		
		
		// 2. Trip이 저장된 후, 해당 tripNo를 사용하여 기본 Detail 일정 추가
		//scheduleService.saveDefault(trip.getTripNo(), trip.getStartDate());
		System.out.println("trip : " + trip);
		model.addAttribute("trip", trip);
		return "/schedule/schedule";
	}
	

	// 일정 목록 페이지로 이동
	@GetMapping("/list")
	public String ScheduleList(@ModelAttribute Trip trip, HttpSession session, Model model) {
		
		Users user = (Users)session.getAttribute("loginUser");
		
		List<Trip> schedules = scheduleService.getMySchedule(user.getUserNo());
		
		model.addAttribute("trip", schedules);
		return "/schedule/list";
	}

	// 일정 내용 페이지로 이동
	@GetMapping("/detail/{tripNo}")
	public String ScheduleDetail(@PathVariable("tripNo") int tripNo, @ModelAttribute Detail detail, HttpSession session, Model model) {
		
		// tripNo를 이용해 여행 정보를 조회
		Trip trip = scheduleService.getTripNo(tripNo);
		
		// 여행 상세 일정 조회
		//List<Detail> tripDetail = scheduleService.getTripDetail(trip.getTripNo());
		scheduleService.getTripDetail(trip.getTripNo());
		System.out.println("trip : " + trip);
		model.addAttribute("trip", trip);	// 여행 정보 추가
		//model.addAttribute("detail", tripDetail);	// 일정 정보 추가
		
		return "/schedule/detail";
	}

	@PostMapping("/saveDetail")
	public String saveDetail(@ModelAttribute Detail detail, Model model) {
		scheduleService.saveDetail(detail);
		
		// 날짜 범위 생성
		List<Date> dateRange = scheduleService.getDateRange(detail.getStartDate(), detail.getEndDate());
		model.addAttribute("dateRange", dateRange);	// 날짜 범위 추가
		
		System.out.println("dateRange : " + dateRange);
		System.out.println("Detail : " + detail);
		
		model.addAttribute("message", "저장이 완료되었습니다.");
		model.addAttribute("error", "저장 중 오류가 발생했습니다.");
		int tripNo = detail.getTripNo();
		// 일정 저장 후에도 기존 데이터 유지하도록 model에 다시 추가
		model.addAttribute("tripNo", detail.getTripNo());
		model.addAttribute("startDate", detail.getStartDate());
		model.addAttribute("endDate", detail.getEndDate());
		model.addAttribute("content", detail.getContent());
		model.addAttribute("placeId", detail.getPlaceId());
		
		return String.format("redirect:/schedule/detail/%d", tripNo);
		//return "schedule/list";
	}

}