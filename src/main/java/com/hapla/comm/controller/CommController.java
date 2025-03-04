package com.hapla.comm.controller;

import java.text.SimpleDateFormat;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hapla.comm.model.service.CommService;
import com.hapla.comm.model.vo.Comm;
import com.hapla.comm.model.vo.Reply;
import com.hapla.common.PageInfo;
import com.hapla.common.Pagination;
import com.hapla.exception.Exception;
import com.hapla.users.model.vo.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        Users loginUser = (Users) session.getAttribute("loginUser");

        if (loginUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        // 로그인한 사용자의 userNo를 Comm 객체에 설정
        c.setUserNo(loginUser.getUserNo());

        int result = commService.insertComm(c);
        
        if (result > 0) {
            return "redirect:/comm/list";
        } else {
            throw new RuntimeException("게시글 작성을 실패하였습니다.");
        }
    }
   
//    @GetMapping("/{id}/{page}")
//    public ModelAndView selectComm(@PathVariable("id") int commNo, @PathVariable("page") int page, HttpSession session, ModelAndView mv) {
//    	Users loginUser = (Users)session.getAttribute("loginUser");
//    	String name = null;
//    	if(loginUser != null) {
//    		name = loginUser.getName();
//    	}
//    	
//    	Comm c = commService.selectComm(commNo, name);
////    	ArrayList<Reply> list = bService.selectReplyList(bId);
//    	
//    	if(c != null) {
////    		mv.addObject("list", list);
//			mv.addObject("c", c).addObject("page", page).setViewName("comm/detail");
//			return mv;
//    	} else {
//    		throw new RuntimeException("게시글 상세조회를 실패하였습니다.");
//    	}
//    }
    
    @GetMapping("/{id}/{page}")
    public ModelAndView selectComm(@PathVariable("id") int commNo, 
                                   @PathVariable("page") int page, 
                                   HttpSession session, 
                                   ModelAndView mv) {
        // 현재 로그인한 사용자 정보 가져오기
        Users loginUser = (Users) session.getAttribute("loginUser");
        String name = (loginUser != null) ? loginUser.getName() : null;

        // 게시글 상세 조회
        Comm c = commService.selectComm(commNo, name);
        ArrayList<Reply> list = commService.selectReplyList(commNo);

        if (c == null) {
        	throw new Exception("실패");
        }

        mv.addObject("c", c).addObject("page", page).setViewName("comm/detail"); // ✅ 게시글 상세 페이지로 이동
        mv.addObject("list", list);

        return mv;
    }
    
    @GetMapping("rinsert")
    @ResponseBody
    public String insertReply(@ModelAttribute Reply r, HttpServletResponse response) {
    	int result = commService.insertReply(r);
    	ArrayList<Reply> list = commService.selectReplyList(r.getCommNo());
    	
    	response.setContentType("application/json; charset=UTF-8");
    	
    	ObjectMapper om = new ObjectMapper();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	om.setDateFormat(sdf);
    	
    	String strJson = null;
    	
    	try {
			strJson = om.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return strJson;
    }
}
