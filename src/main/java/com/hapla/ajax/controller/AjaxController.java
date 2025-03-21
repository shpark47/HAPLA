package com.hapla.ajax.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.hapla.schedule.model.service.ScheduleService;
import com.hapla.users.model.vo.Users;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@SessionAttributes("loginUser")
public class AjaxController {
	
	private final ScheduleService scheduleService;
	
	@PostMapping("/schedule/saveDetail")
	public String saveDetail(Model model, @RequestBody List<Map<String, List<String>>> maps) throws ParseException {
	    Users loginUser = (Users) model.getAttribute("loginUser");
		Set<String> dateSet = maps.getFirst().keySet();
		ArrayList<String> dates = new ArrayList<>(dateSet);

		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
		Date date = sdf.parse(dates.get(0));
		String date1 = sdf.format(date);

		System.out.println(date1);
		System.out.println(maps.getLast());
		return null;
	}
}
