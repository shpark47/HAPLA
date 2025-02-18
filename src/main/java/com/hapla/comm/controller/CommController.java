package com.hapla.comm.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hapla.common.Pagination;
import com.hapla.comm.model.service.CommService;
import com.hapla.comm.model.vo.Comm;
import com.hapla.comm.model.vo.PageInfo;

import jakarta.servlet.http.HttpServletRequest;
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
}
