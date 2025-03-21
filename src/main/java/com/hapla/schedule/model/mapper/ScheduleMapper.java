package com.hapla.schedule.model.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

	Integer getTripNoByUser(int userNo);	// tripNo 조회
	
	void saveDetail(Detail detail); 	// Detail 저장

	void insertMemo(@Param("detailNo") int detailNo, @Param("content") String content);
    
	void insertPlace(@Param("detailNo") int detailNo, @Param("placeId") String placeId);

	List<Trip> selectTripByTripNo(int tripNo);

	List<Detail> selectDetailsByTripNo(int tripNo);

	Map<Integer, String> selectMemosByTripNo(int tripNo);

	Map<Integer, List<String>> selectPlacesByTripNo(int tripNo);

	int deleteTrip(int tripNo);
	
	
}
