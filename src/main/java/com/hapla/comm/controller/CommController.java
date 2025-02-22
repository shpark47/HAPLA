package com.hapla.comm.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hapla.comm.model.service.CommService;
import com.hapla.comm.model.vo.Comm;
import com.hapla.comm.model.vo.PageInfo;
import com.hapla.common.Pagination;
import com.hapla.users.model.vo.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comm")
public class CommController {
    private final CommService commService;
    
    @GetMapping("list")
	public String selectList(@RequestParam(value="page", defaultValue="1") int currentPage, Model model, HttpServletRequest request) {
//		int currentPage = 1;
//		if(page != null ) {
//			currentPage = Integer.parseInt(page);
//		}
		
		int listCount = commService.getListCount(1);
		
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
		ArrayList<Comm> list = commService.selectCommList(pi, 1);
		
		model.addAttribute("list", list).addAttribute("pi", pi);
		model.addAttribute("loc", request.getRequestURI());
		// getRequestURI() : /board/list
		// getRequestURL() : http://localhost:8080/board/list 
		
		return "comm/list";
	}
    
    @GetMapping("write")
	public String writeComm() {
		return "comm/write";
	}
    
    @PostMapping("insert")
	public String insertComm(@ModelAttribute Comm c, HttpSession session) {
    	c.setUserNo(((Users)session.getAttribute("loginUser")).getUserNo());
		int result = commService.insertComm(c);
		
		if(result > 0) {
			return "redirect:/comm/list";
		} else {
			throw new RuntimeException("게시글 작성을 실패하였습니다.");
		}
		
	}
   
    
}
