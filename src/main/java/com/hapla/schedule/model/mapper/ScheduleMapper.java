package com.hapla.schedule.model.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hapla.schedule.model.vo.Detail;
import com.hapla.schedule.model.vo.Trip;

@Mapper
public interface ScheduleMapper {

	void saveTrip(Trip trip);

	List<Trip> getMySchedule(int userNo);

	List<Detail> getTripDetail(int tripNo);

	Trip getTripNo(int tripNo);

	// 날짜 범위에 맞는 일정을 DB에서 가져오는 쿼리
	List<Trip> selectScheduleByDateRange(Date startDate, Date endDate);
}
