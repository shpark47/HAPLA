package com.hapla.schedule.model.mapper;

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
}
