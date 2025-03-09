package com.hapla.review.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.hapla.comm.model.service.CommService;
import com.hapla.comm.model.vo.Reply;
import com.hapla.review.model.service.ReviewService;
import com.hapla.review.model.vo.Review;
import com.hapla.users.model.vo.Users;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/review", "/comm"})
@RequiredArgsConstructor
@SessionAttributes("loginUser")
public class ReviewAjaxController {
	
	private final ReviewService reviewService;
	private final CommService commService;
	
	@PostMapping("insert")
    public Map<String, Object> insertReview(@RequestBody HashMap<String, String> map, 
                                            @ModelAttribute Review r, 
                                            HttpSession session) {  
        Map<String, Object> response = new HashMap<>();
        Users loginUser = (Users) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.put("status", "error");
            response.put("message", "로그인이 필요합니다.");
            return response;  // ✅ JSON 응답 반환
        }

        // ⭐ @ModelAttribute Review r로 이미 일부 값 자동 매핑됨
        // @RequestBody HashMap<String, String>을 통해 나머지 값 덮어쓰기 (우선순위 적용)
        r.setUserNo(loginUser.getUserNo());  // 세션에서 유저 정보 추가
        r.setTitle(map.getOrDefault("title", r.getTitle() != null ? r.getTitle() : "제목 없음"));
        r.setContent(map.getOrDefault("content", r.getContent() != null ? r.getContent() : "내용 없음"));

        // ⭐ rating은 Integer로 변환 필요
        int rating;
        try {
            rating = Integer.parseInt(map.getOrDefault("rating", String.valueOf(r.getRating() > 0 ? r.getRating() : 3)));  
        } catch (NumberFormatException e) {
            rating = 3;
        }
        r.setRating(rating);

        r.setWhen(map.getOrDefault("when", r.getWhen() != null ? r.getWhen() : "최근 1개월 이내"));
        r.setWithWhom(map.getOrDefault("withWhom", r.getWithWhom() != null ? r.getWithWhom() : "혼자"));

        // 데이터 저장
        int result = reviewService.insertReview(r);

        if (result > 0) {
            response.put("status", "success");
            response.put("message", "리뷰가 성공적으로 저장되었습니다.");
        } else {
            response.put("status", "error");
            response.put("message", "게시글 작성을 실패하였습니다.");
        }

        return response;
    }
	
	@PostMapping("reply")
	public ArrayList<Reply> insertReply(@ModelAttribute Reply r, HttpServletResponse response){
		System.out.println(r);
		int result = commService.insertReply(r);
		ArrayList<Reply> list = commService.selectReplyList(r.getCommNo());
		return list;
	}
	
	@DeleteMapping("reply")
	public int deleteReply(@RequestParam("replyNo") int replyNo) {
		return commService.deleteReply(replyNo);

	}

	@PutMapping("reply")
	public int updateReply(@ModelAttribute Reply r) {
		int result = commService.updateReply(r);
		return result;
	}
}