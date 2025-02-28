package com.hapla.schedule.model.service;

import org.springframework.stereotype.Service;

import com.hapla.schedule.model.mapper.ScheduleMapper;
import com.hapla.schedule.model.vo.Trip;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	
	private final ScheduleMapper scheduleMapper;
	
	// 일정 저장 메소드
	public void saveTrip(Trip trip) {
		scheduleMapper.saveTrip(trip);
	}

}
