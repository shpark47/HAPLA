package com.hapla.schedule.model.service;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hapla.schedule.model.mapper.ScheduleMapper;
import com.hapla.schedule.model.vo.Detail;
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

	// 일정 저장 메소드 (기본값)
//	public void saveDefault(int tripNo, Date startDate) {
//		scheduleMapper.saveDefalut(tripNo);
//	}
	
	public Trip getTripNo(int tripNo) {
		return scheduleMapper.getTripNo(tripNo);
	}
	
	public List<Trip> getMySchedule(int userNo) {
		return scheduleMapper.getMySchedule(userNo);
	}

	public List<Detail> getTripDetail(int userNo) {
		return scheduleMapper.getTripDetail(userNo);
	}


}
