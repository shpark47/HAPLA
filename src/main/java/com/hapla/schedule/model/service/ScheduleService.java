package com.hapla.schedule.model.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public List<Detail> getTripDetail(int tripNo) {
		return scheduleMapper.getTripDetail(tripNo);
	}

	/*
	 * @Transactional // 여러개의 데이터를 db에 저장할때 오류발생시 데이터 저장이 롤백되게 해줌 public void
	 * saveDetails(List<Detail> details, int userNo) { Integer tripNo =
	 * scheduleMapper.getTripNoByUser(userNo); // tripNo조회
	 * 
	 * if(tripNo == null) { throw new
	 * IllegalArgumentException("사용자의 여행 정보를 찾을 수 없습니다."); // tripNo 없으면 예외 } // 여행
	 * 상세 정보 저장 for(Detail detail : details) { detail.setTripNo(tripNo);
	 * scheduleMapper.saveDetail(detail); } }
	 */

	public String saveDetails(List<Detail> details, Map<String, List<String>> placeMap, Map<String, String> memoMap) {
        int insertedCount = 0;
        System.out.println(" 저장된 detail : " + details);
        
        for (Detail detail : details) {
            // 1. DETAIL 테이블 저장 (자동 생성된 DETAIL_NO 가져오기)
        	System.out.println("✅ 저장된 detailNo = " + detail.getDetailNo());
            scheduleMapper.saveDetail(detail);
            int detailNo = detail.getDetailNo();

            
            System.out.println("✅ 저장된 detailNo = " + detail.getDetailNo());
            
            // 2. MEMO 데이터 저장 (해당 날짜에 메모가 존재하면)
            if (memoMap.containsKey(detail.getSelectDate().toString())) {
                scheduleMapper.insertMemo(detailNo, memoMap.get(detail.getSelectDate().toString()));
            }

            // 3. PLACE 데이터 저장 (해당 날짜에 장소가 존재하면)
            if (placeMap.containsKey(detail.getSelectDate().toString())) {
                for (String placeId : placeMap.get(detail.getSelectDate().toString())) {
                    scheduleMapper.insertPlace(detailNo, placeId);
                }
            }

            insertedCount++;
        }

        return insertedCount > 0 ? "저장 성공!" : "저장 실패!";
    }
}
