package com.hapla.schedule.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.hapla.schedule.model.vo.Trip;

@Mapper
public interface ScheduleMapper {

	void saveTrip(Trip trip);
}
