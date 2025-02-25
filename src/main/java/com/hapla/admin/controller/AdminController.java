package com.hapla.admin.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hapla.admin.model.service.AdminService;
import com.hapla.admin.model.vo.AdminUsers;
import com.hapla.comm.model.vo.Comm;
import com.hapla.common.PageInfo;
import com.hapla.common.Pagination;
import com.hapla.users.model.vo.Users;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {
	
	private final AdminService aService;
	
	// 사용자 조회
	@GetMapping("admin/users") 
	public String selectUserList(@RequestParam(value="userPage", defaultValue = "1") int currentPage, @RequestParam(value="userkeyword",required=false) String keyword, Model model, HttpServletRequest request) {
		// 전체 사용자 수 조회 
		int listCount = aService.userListCount(keyword);
		
		PageInfo userPi = Pagination.getPageInfo(currentPage, listCount, 5);
		
		// 사용자 목록 조회
		ArrayList<Users> list = aService.selectUserList(userPi,keyword);
		
		// 총 정보 조회( 총 사용자, 총 게시글, 총 리뷰)
		int totalUsers = aService.userListCount(null);
		int totalComm = aService.totalComm(keyword);
		int totalReview = aService.totalReview(keyword);
		
		model.addAttribute("list", list);
		model.addAttribute("userPi", userPi);
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalComm", totalComm);
		model.addAttribute("totalReview", totalReview);
		model.addAttribute("loc", request.getRequestURI());
		model.addAttribute("userkeyword", keyword);

		return "/admin/users";
	}
	
	//사용자 활동 조회
	@GetMapping("admin/write")
	public String selectUserWrite(@RequestParam(value="writePage", defaultValue = "1") int currentPage, @RequestParam(value="writekeyword",required=false) String keyword, Model model, HttpServletRequest request) {
		// 사용자 수 조회 
		int listCount = aService.userWrite(keyword);
		
		PageInfo writePi = Pagination.getPageInfo(currentPage, listCount, 5);
		
		// 목록 조회
		ArrayList<AdminUsers> alist = aService.selectUserWriteList(writePi,keyword);
		
		int totalUsers = aService.userWrite(keyword); 
	    int totalComm = aService.totalComm(keyword);
	    int totalReview = aService.totalReview(keyword);
		
		
		model.addAttribute("alist", alist);
		model.addAttribute("writePi", writePi);
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalComm", totalComm);
		model.addAttribute("totalReview", totalReview);
		model.addAttribute("loc", request.getRequestURI());
		model.addAttribute("writekeyword", keyword);
		
		return "admin/users";
	}
		
	@GetMapping("admin/report") 
	public String report(@RequestParam (value="page", defaultValue = "1") int CurrentPage,@RequestParam(value="keyword",required=false)String keyword,Model model) {
		int listCount = aService.userReportCount(keyword);
		
		PageInfo pi = Pagination.getPageInfo(CurrentPage, listCount, 5);
		ArrayList<AdminUsers> clist = aService.selectCommList(pi,keyword); 
		
		model.addAttribute("clist", clist);
		
		return "/admin/report";
	}
	
	@GetMapping("admin/notice") 
	public String notice() {
		return "/admin/notice";
	}
	@GetMapping("admin/noticeWrite") 
	public String noticeWrite() {
		return "/admin/noticeWrite";
	}
	@GetMapping("admin/noticeEdit")
	public String noticeEdit() {
		return "/admin/noticeEdit";
	}
	@GetMapping("admin/stats")
	public String stats() {
		return "/admin/stats";
	}
	@GetMapping("admin/booking")
	public String booking() {
		return "/admin/booking";
	}
	@GetMapping("admin/revenue")
	public String revenue() {
		return "/admin/revenue";
	}	
}
