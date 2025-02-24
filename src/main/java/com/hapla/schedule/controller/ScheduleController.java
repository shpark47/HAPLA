package com.hapla.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.hapla.schedule.model.service.ScheduleService;

import ch.qos.logback.core.model.Model;

@Controller
public class ScheduleController {
	
	private final ScheduleService scheduleService;
	
	// 생성자 Autowired 주입
	public ScheduleController(ScheduleService scheduleService) {
		this.scheduleService = scheduleService;
	}
	
	@GetMapping("/schedule/scheduleCalendar")
	public String ScheduleCalendar() {
		return "/schedule/scheduleCalendar";
	}
	
	@GetMapping("/schedule/schedule")
	public String Schedule() {
		return "/schedule/schedule";
	}
}
