package com.hapla.ajax.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.hapla.schedule.model.service.ScheduleService;
import com.hapla.schedule.model.vo.Detail;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@SessionAttributes("loginUser")
public class AjaxController {
	
	private final ScheduleService scheduleService;
	
//	@PostMapping("/schedule/saveDetail")
//	public String saveDetail(Model model, @RequestBody List<Map<String, List<String>>> maps) throws ParseException {
//	    Users loginUser = (Users) model.getAttribute("loginUser");
//		
//	    Set<String> dateSet = maps.getFirst().keySet();
//		ArrayList<String> dates = new ArrayList<>(dateSet);
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
//		Date date = sdf.parse(dates.get(0));
//		String date1 = sdf.format(date);
//
//		System.out.println(date1);
//		System.out.println(maps.getLast());
//		return null;
//	}
	
	@PostMapping("/schedule/saveDetail")
	public String saveDetail(Model model, @RequestBody List<Map<String, Object>> maps) throws ParseException {

	    int tripNo = 1; // 프론트에서 받아와야 하는 값(임시 값)

	    // ✅ 날짜 추출 로직 결합
	    Set<String> dateSet = maps.get(0).keySet();
	    ArrayList<String> dates = new ArrayList<>(dateSet);

	    SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
	    Date date = sdf.parse(dates.get(0));
	    String date1 = sdf.format(date);

	    System.out.println("📅 변환된 날짜 형식: " + date1);
	    System.out.println("📦 전체 maps 구조: " + maps);

	    // ✅ placeMap 변환
	    Map<String, Object> rawPlaceMap = maps.get(0);
	    Map<String, List<String>> placeMap = new HashMap<>();
	    for (Map.Entry<String, Object> entry : rawPlaceMap.entrySet()) {
	        String key = entry.getKey();
	        Object value = entry.getValue();
	        if (value instanceof List<?>) {
	            List<?> rawList = (List<?>) value;
	            List<String> placeIds = new ArrayList<>();
	            for (Object item : rawList) {
	                if (item instanceof String) {
	                    placeIds.add((String) item);
	                }
	            }
	            placeMap.put(key, placeIds);
	        }
	    }

	    // ✅ memoMap 변환
	    Map<String, Object> rawMemoMap = maps.get(1);
	    Map<String, String> memoMap = new HashMap<>();
	    for (Map.Entry<String, Object> entry : rawMemoMap.entrySet()) {
	        if (entry.getValue() instanceof String) {
	            memoMap.put(entry.getKey(), (String) entry.getValue());
	        }
	    }

	    // ✅ Detail 리스트 생성
	    List<Detail> detailList = new ArrayList<>();

	    for (String dateKey : placeMap.keySet()) {
	        if (dateKey == null || !dateKey.matches("\\d{4}-\\d{2}-\\d{2}")) continue;
	        java.sql.Date sqlDate = java.sql.Date.valueOf(dateKey);
	        Detail detail = new Detail();
	        detail.setTripNo(tripNo);
	        detail.setSelectDate(sqlDate);
	        detailList.add(detail);
	    }

	    for (String dateKey : memoMap.keySet()) {
	        if (dateKey == null || !dateKey.matches("\\d{4}-\\d{2}-\\d{2}")) continue;
	        java.sql.Date sqlDate = java.sql.Date.valueOf(dateKey);
	        Detail detail = new Detail();
	        detail.setTripNo(tripNo);
	        detail.setSelectDate(sqlDate);
	        detail.setContent(memoMap.get(dateKey));
	        detailList.add(detail);
	    }
	    
	    System.out.println(detailList);
	    // ✅ DB 저장 호출
	    return scheduleService.saveDetails(detailList, placeMap, memoMap);
	}
}

