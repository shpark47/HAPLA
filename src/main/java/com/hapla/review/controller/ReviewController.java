package com.hapla.review.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hapla.comm.model.vo.Comm;
import com.hapla.common.PageInfo;
import com.hapla.common.Pagination;
import com.hapla.review.model.service.ReviewService;
import com.hapla.review.model.vo.Review;
import com.hapla.users.model.vo.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
	private final ReviewService reviewService;
	
	@GetMapping("main")
	public String toMain() {
		return "review/main";
	}
	
	@GetMapping("list")
	public String selectList(@RequestParam(value="page", defaultValue="1") int currentPage, Model model, HttpServletRequest request) {
//		int currentPage = 1;
//		if(page != null ) {
//			currentPage = Integer.parseInt(page);
//		}
		
		int listCount = reviewService.getListCount(1);
		
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
		ArrayList<Comm> list = reviewService.selectReviewList(pi, 1);
		
		model.addAttribute("list", list).addAttribute("pi", pi);
		model.addAttribute("loc", request.getRequestURI());
		return "review/list";
	}
	
	@GetMapping("write")
	public String writeReview() {
		return "review/write";
	}

//	@PostMapping("insert")
//	@ResponseBody
//	public String insertReview(@RequestParam(value = "rating", required = false) Integer rating, 
//	                           @RequestParam(value = "content", required = false, defaultValue = "내용 없음") String content, 
//	                           @ModelAttribute Review r, 
//	                           HttpSession session) {
//	    Users loginUser = (Users) session.getAttribute("loginUser");
//
//	    if (loginUser == null) {
//	        throw new RuntimeException("로그인이 필요합니다.");
//	    }
//
//	    // 로그인한 사용자의 userNo 설정
//	    r.setUserNo(loginUser.getUserNo());
//
//	    // ⭐️ rating이 null이면 기본값(3) 설정
//	    r.setRating(rating != null ? rating : 3);
//
//	    // ⭐️ content가 비어 있으면 기본값 설정
//	    if (content.trim().isEmpty()) {
//	        content = "내용 없음"; // ✅ 기본값 설정
//	    }
//	    r.setContent(content);
//
//	    int result = reviewService.insertReview(r);
//	    
//	    if (result > 0) {
//	        return "redirect:/review/list";
//	    } else {
//	        throw new RuntimeException("게시글 작성을 실패하였습니다.");
//	    }
//	}



	
	@GetMapping("/{id}/{page}")
    public ModelAndView selectComm(@PathVariable("id") int reviewNo, 
                                   @PathVariable("page") int page, 
                                   HttpSession session, 
                                   ModelAndView mv) {
        // 현재 로그인한 사용자 정보 가져오기
        Users loginUser = (Users) session.getAttribute("loginUser");
        String name = (loginUser != null) ? loginUser.getName() : null;

        // 게시글 상세 조회
        Review r = reviewService.selectReview(reviewNo);
//        ArrayList<Reply> list = reviewService.selectReplyList(commNo);

        if (r == null) {
            mv.addObject("message", "존재하지 않는 게시글입니다.").setViewName("error/404"); // ✅ 사용자 친화적인 에러 페이지로 이동
            return mv;
        }

        mv.addObject("r", r).addObject("page", page).setViewName("review/detail"); // ✅ 게시글 상세 페이지로 이동
//        mv.addObject("list", list);

        return mv;
    }
}
