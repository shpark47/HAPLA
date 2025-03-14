package com.hapla.review.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.hapla.comm.model.vo.Comm;
import com.hapla.common.PageInfo;
import com.hapla.common.Pagination;
import com.hapla.exception.Exception;
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
		
//		System.out.println("현재 요청된 페이지 번호: " + currentPage);
		
		int boardLimit = 4;
		int listCount = reviewService.getListCount();
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, boardLimit);
		
//		System.out.println("전체 리뷰 개수: " + listCount + ", 시작 페이지: " + pi.getStartPage() + ", 마지막 페이지: " + pi.getEndPage() + ", 한 페이지 최대 개수 : " + boardLimit);
		
		ArrayList<Review> list = reviewService.selectReviewList(pi);
		
		model.addAttribute("list", list).addAttribute("pi", pi);
		model.addAttribute("loc", request.getRequestURI());
		return "review/list";
	}
	
	@GetMapping("write")
	public String writeReview() {
		return "review/write";
	}

	@GetMapping("/{id}/{page}")
    public ModelAndView selectReview(@PathVariable("id") int reviewNo, 
                                     @PathVariable("page") int page,
                                     HttpSession session) throws Exception {
        ModelAndView mv = new ModelAndView();

        // 현재 로그인한 사용자 정보 가져오기
        Users loginUser = (Users) session.getAttribute("loginUser");
        String name = (loginUser != null) ? loginUser.getName() : null;

        // 게시글 상세 조회
        Review r = reviewService.selectReview(reviewNo);

        if(r != null) {
	    	List<String> imageUrls = null;
	        String thumbnail = null;
	
	        if (r.getImageUrls() != null && !r.getImageUrls().isEmpty()) {
	            String[] imageUrlsArray = r.getImageUrls().split(",");
	            System.out.println("imageUrlsArray : " + Arrays.toString(imageUrlsArray));
	
	            if (imageUrlsArray.length > 0) {
	                thumbnail = imageUrlsArray[0]; // 첫 번째 이미지를 썸네일로 사용
	                imageUrls = Arrays.asList(imageUrlsArray).subList(1, imageUrlsArray.length); // 나머지 상세 이미지
	            }
	        }
	
	        // ModelAndView에 데이터 추가
	        mv.addObject("r", r)
	          .addObject("page", page)
	          .addObject("thumbnail", thumbnail)
	          .addObject("imageUrls", imageUrls)
	          .setViewName("review/detail");
	
	        return mv;
	    } else {
	    	throw new Exception("리뷰 조회 실패");
	    }
    }

	@PostMapping("insert")
	public String insertReview(@ModelAttribute Review r) {

		System.out.println(r);

		if (r.getImageUrls() != null) {
			String img = r.getImageUrls();
			if (img.contains(",")){
				String tumbnail = img.split(",")[0];
				r.setThumnail(tumbnail);
				r.setImageUrls(img);
			}else{
				r.setThumnail(r.getImageUrls());
				r.setImageUrls(null);
			}
		}

		// 데이터 저장
		int result = reviewService.insertReview(r);
		if (result == 1) {
			return "redirect:/review/main";
		}
		throw new Exception("실패");
	}
}
