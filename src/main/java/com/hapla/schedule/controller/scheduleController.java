package com.hapla.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class scheduleController {
	
	@GetMapping("/scheduleCalendar")
	public String showScheduleCalendar() {
		return "scheduleCalendar";
	}
}
