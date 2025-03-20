package com.hapla.ajax.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.hapla.schedule.model.service.ScheduleService;
import com.hapla.schedule.model.vo.Detail;
import com.hapla.users.model.vo.Users;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/schedule"})
@RequiredArgsConstructor
@SessionAttributes("loginUser")
public class AjaxController {
	
	private final ScheduleService scheduleService;
	
//	@PostMapping("/saveDetail")
//	public String saveDetail(HttpSession session,@RequestBody ArrayList<Detail> details) {
//	    Users loginUser = (Users) session.getAttribute("loginUser");
//	    System.out.println(loginUser.getUserNo());
//	    System.out.println("잘 들어옴");
//	    System.out.println(details);
//	    for(Detail dt : details) {
//	    	System.out.println(dt); 
//	    }
//	    try {
//	        return "여행 데이터 저장 완료!";
//	    } catch (Exception e) {
//	        return "저장 중 오류 발생!";
//	    }
//	}
	
	@PostMapping(value = "/saveDetail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String saveDetail(HttpSession session, @RequestBody Map<String, List<Detail>> requestData) {
	    Users loginUser = (Users) session.getAttribute("loginUser");
	    
	    //System.out.println("로그인한 유저 번호: " + loginUser.getUserNo());
	    
	    // 로그인 체크
	    if(loginUser == null) {
	    	return "로그인이 필요합니다.";
	    }
	    
	    // 데이터가 비어 있는 경우
	    List<Detail> details = requestData.get("details");
	    if (details == null || details.isEmpty()) {
	        return "데이터가 비어있습니다.";
	    }

	    try {
            scheduleService.saveDetails(details, loginUser.getUserNo());
            return "여행 데이터 저장 완료!";
        } catch (Exception e) {
        	e.printStackTrace();
            return "저장 중 오류 발생!";
        }

	}
	
}
