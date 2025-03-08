package com.hapla.schedule.model.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	
	public List<Date> getDateRange(Date startDate, Date endDate) {
	    List<Date> dates = new ArrayList<>();
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(startDate);

	    // 날짜 범위에 맞춰 java.util.Date로 변환하여 리스트에 추가
	    while (!calendar.getTime().after(endDate)) {
	        dates.add(calendar.getTime());  // java.util.Date로 추가
	        calendar.add(Calendar.DATE, 1);  // 하루씩 증가
	    }
	    return dates;
	}
	
	// DB에서 날짜 범위에 맞는 일정을 조회하는 메소드
	public List<Trip> getSchedulesByDateRange(Date startDate, Date endDate){
		return scheduleMapper.selectScheduleByDateRange(startDate, endDate);
	}
	
	
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
