package com.hapla.ajax.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//		System.out.println(maps.get());
//		return null;
//	}

	@PostMapping("/schedule/saveDetail")
	public String saveDetail(@RequestBody Map<String, Object> data) throws ParseException {

		int tripNo = (int) data.get("tripNo");
	    List<Map<String, Object>> maps = (List<Map<String, Object>>) data.get("datas");
		 
		
	    // 날짜 포맷 준비
	    SimpleDateFormat fromFormat = new SimpleDateFormat("yy/MM/dd");
	    SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");

	    // ✅ placeMap 변환 및 날짜 포맷 변경
	    Map<String, Object> rawPlaceMap = maps.get(0);
	    Map<String, List<String>> placeMap = new HashMap<>();

	    for (Map.Entry<String, Object> entry : rawPlaceMap.entrySet()) {
	        String originalKey = entry.getKey(); // ex: 25/03/24
	        String convertedKey;
	        try {
	            Date parsed = fromFormat.parse(originalKey);
	            convertedKey = toFormat.format(parsed); // ex: 2025-03-24
	        } catch (ParseException e) {
	            continue;
	        }

	        Object value = entry.getValue();
	        if (value instanceof List<?>) {
	            List<?> rawList = (List<?>) value;
	            List<String> placeIds = new ArrayList<>();
	            for (Object item : rawList) {
	                if (item instanceof String) {
	                    placeIds.add((String) item);
	                }
	            }
	            placeMap.put(convertedKey, placeIds); // ✅ 변환된 key 사용
	        }
	    }

	    // ✅ memoMap 변환 및 날짜 포맷 변경
	    Map<String, Object> rawMemoMap = maps.get(1);
	    Map<String, String> memoMap = new HashMap<>();

	    for (Map.Entry<String, Object> entry : rawMemoMap.entrySet()) {
	        String originalKey = entry.getKey();
	        String convertedKey;
	        try {
	            Date parsed = fromFormat.parse(originalKey);
	            convertedKey = toFormat.format(parsed);
	        } catch (ParseException e) {
	            continue;
	        }

	        Object value = entry.getValue();
	        if (value instanceof List<?>) {
	            List<?> list = (List<?>) value;
	            if (!list.isEmpty() && list.get(0) instanceof String) {
	                memoMap.put(convertedKey, (String) list.get(0));
	            }
	        } else if (value instanceof String) {
	            memoMap.put(convertedKey, (String) value);
	        }
	    }

	    // ✅ Detail 리스트 생성
	    List<Detail> detailList = new ArrayList<>();

	    for (String dateKey : placeMap.keySet()) {
	        java.sql.Date sqlDate = java.sql.Date.valueOf(dateKey);
	        List<String> places = placeMap.get(dateKey);

	        for (String placeId : places) {
	            Detail detail = new Detail();
	            detail.setTripNo(tripNo);
	            detail.setSelectDate(sqlDate);
	            detail.setPlaceId(placeId); // ✅ String 단건 넣기
	            detailList.add(detail);
	        }
	    }

	    for (String dateKey : memoMap.keySet()) {
	        java.sql.Date sqlDate = java.sql.Date.valueOf(dateKey);
	        Detail detail = new Detail();
	        detail.setTripNo(tripNo);
	        detail.setSelectDate(sqlDate);
	        detail.setContent(memoMap.get(dateKey));
	        detailList.add(detail);
	    }

	    System.out.println("✅ 저장할 Detail 리스트: " + detailList);
	    System.out.println("🧾 변환된 placeMap: " + placeMap);
	    System.out.println("🧾 변환된 memoMap: " + memoMap);
	    
	    // 최종 저장
	    return scheduleService.saveDetails(detailList, placeMap, memoMap);
	}

}

