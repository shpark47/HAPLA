package com.hapla.admin.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hapla.admin.model.service.AdminService;
import com.hapla.admin.model.vo.AdminUsers;
import com.hapla.admin.model.vo.DailyStats;
import com.hapla.admin.model.vo.Notice;
import com.hapla.admin.model.vo.Report;
import com.hapla.comm.model.vo.Comm;
import com.hapla.comm.model.vo.Reply;
import com.hapla.common.PageInfo;
import com.hapla.common.Pagination;
import com.hapla.review.model.vo.Review;
import com.hapla.users.model.vo.Users;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {
	
	private final AdminService aService;
	
	
	@GetMapping("/admin/index")
	public String index() {
		return "/admin/index";
	}
	
	@GetMapping("/admin/members")
	public String members(@RequestParam(value="userPage", defaultValue = "1") int currentPage, @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value="order",defaultValue = "new") String order, Model model,HttpServletRequest request ) {
		
		
		// 전체 회원 수 조회
		int listCount = aService.userListCount(keyword);
		
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 7);
		
		// 회원 목록 조회
		ArrayList<AdminUsers> list = aService.selectUserList(pi, keyword, order);
		
		
		model.addAttribute("list", list);
		model.addAttribute("pi", pi);
		model.addAttribute("keyword", keyword);
		model.addAttribute("order", order);
		model.addAttribute("loc", request.getRequestURI());
		return "/admin/members";
	}
	
	
	@GetMapping("admin/userStats")
	public String userStats( Model model, @RequestParam(value="page",defaultValue="1")int currentPage, @RequestParam(value="nickname",required=false) String nickname, 
							@RequestParam(value="order", defaultValue="new")String order,@RequestParam(value="type",defaultValue="comm") String type, HttpServletRequest request) {
		
		// 전체 데이터 조회
		int totalUsers = aService.totalUsersCount();
		int totalComm = aService.totalComm();
		int totalReply = aService.totalReplyCount();
		int totalReview = aService.totalReview();
		
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalComm", totalComm);
		model.addAttribute("totalReply", totalReply);
		model.addAttribute("totalReview", totalReview);
		
		//타입에 따른 목록 조회
		if("comm".equals(type)) {
			int listCount = aService.commListCount(nickname);
			PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
			ArrayList<Comm> list = aService.selectComm(pi,nickname,order);
			
			model.addAttribute("list", list);
			model.addAttribute("pi", pi);
			
		}else if("reply".equals(type)){
			int listCount = aService.replyListCount(nickname);
			PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
			ArrayList<Reply> list = aService.selectReply(pi,nickname,order);
			
			model.addAttribute("list", list);
			model.addAttribute("pi", pi);
			
		}else if("review".equals(type)) {
			int listCount = aService.reviewListCount(nickname);
			PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
			ArrayList<Review> list = aService.selectReview(pi,nickname,order);
			
			model.addAttribute("list", list);
			model.addAttribute("pi", pi);
		}
		
		model.addAttribute("nickname", nickname);
		model.addAttribute("order", order);
		model.addAttribute("type", type);
		model.addAttribute("loc", request.getRequestURI());
		
		List<String> headers = aService.headers(type);
		model.addAttribute("headers", headers);
		
		return "/admin/userStats";
	}	
	
	
	@PostMapping("admin/deleteComm")
	@ResponseBody
	public HashMap<String, Boolean> deleteComm(@RequestParam("commNo") int commNo) {
		boolean success = aService.deleteComm(commNo);
		HashMap<String,Boolean> map = new HashMap<String,Boolean>();
		map.put("success", success);
		return map;
	}
	
	@PostMapping("admin/deleteReview")
	@ResponseBody
	public HashMap<String, Boolean> deleteReview(@RequestParam("reviewNo") int reviewNo) {
		boolean success = aService.deleteReview(reviewNo);
		HashMap<String,Boolean> map = new HashMap<String,Boolean>();
		map.put("success", success);
		return map;
	}
	
	@PostMapping("admin/deleteReply")
	@ResponseBody
	public HashMap<String, Boolean> deleteReply(@RequestParam("replyNo") int replyNo) {
		boolean success = aService.deleteReply(replyNo);
		HashMap<String,Boolean> map = new HashMap<String,Boolean>();
		map.put("success", success);
		return map;
	}
	
	@PostMapping("admin/memberUpdate")
	@ResponseBody 
	public String updateMember(@ModelAttribute Users user) {
	    int result = aService.updateMember(user);
        return result > 0 ? "success" : "fail";
	}
	
	
	@GetMapping("admin/report") 
	public String report(Model model,@RequestParam(value="page",defaultValue="1") int currentPage,HttpServletRequest request,@RequestParam(value="keyword",required=false) String keyword, 
						@RequestParam(value="order",defaultValue="new") String order) {
		int listCount = aService.reportListCount();
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
		
		ArrayList<Report> list = aService.selectReportList(pi,keyword,order);
		int totalWait = aService.totalWait();
		int totalAccept = aService.totalAccept();
		int totalReject = aService.totalReject();
		
		model.addAttribute("list", list);
		model.addAttribute("pi", pi);
		model.addAttribute("loc", request.getRequestURI());
		model.addAttribute("totalWait", totalWait);
		model.addAttribute("totalAccept", totalAccept);
		model.addAttribute("totalReject", totalReject);
		return "/admin/report";
	}
	
	@PostMapping("admin/status")
	@ResponseBody
	public String updateStatus(@ModelAttribute Report report, @RequestParam("reportNo") int reportNo, @RequestParam("reportStatus") String reportStatus) {
	    try {
	        report.setReportNo(reportNo);
	        report.setReportStatus(reportStatus);
	        
	        // 신고 상태 업데이트
	        int result = aService.updateStatus(report);
	        
	        // 승인된 경우 해당 게시글/댓글 삭제
	        if (reportStatus.equals("A")) {
	            // 신고 대상 카테고리 확인 후 삭제 처리
	            Report reportDetail = aService.getReportDetail(reportNo);
	            if (reportDetail != null) {
	                if ("C".equals(reportDetail.getReportCategory())) {
	                    // 게시글인 경우
	                    int commNo = aService.getCommNo(reportNo);
	                    if (commNo > 0) {
	                        aService.deleteCommReport(commNo);
	                    }
	                } else if ("R".equals(reportDetail.getReportCategory())) {
	                    // 댓글인 경우
	                    int replyNo = aService.getReplyNo(reportNo);
	                    if (replyNo > 0) {
	                        aService.deleteReplyReport(replyNo);
	                    }
	                }
	            }
	        }
	        
	        return result > 0 ? "success" : "fail";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "error";
	    }
	}
	// 공지사항 상세 페이지
//	@GetMapping("admin/detail/{id}/{page}")
//	public String detail(@PathVariable("id") int noticeNo, @PathVariable("page") int page, Model model) {
//	    Notice notice = aService.selectNotice(noticeNo);
//	    if(notice == null) {
//	        return "redirect:/admin/notice";
//	    }
//	    model.addAttribute("notice", notice);
//	    model.addAttribute("page", page);
//	    return "/admin/detail";
//	}
	// 공지사항 상세 페이지
	@GetMapping("admin/detail/{id}/{page}")
	public ModelAndView detail(@PathVariable("id") int noticeNo, @PathVariable("page") int page, ModelAndView mv) {
	    Notice notice = aService.selectNotice(noticeNo);
	    mv.addObject("notice", notice).addObject("page", page).setViewName("admin/detail");
	    return mv;
	}
	
	// 공지사항 목록 페이지
	@GetMapping("admin/notice") 
	public String notice(@RequestParam(value="page",defaultValue="1")int currentPage,Model model) {
		int listCount = aService.noticeCount();
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 3);
		
		ArrayList<Notice> list = aService.selectNoticeList(pi);
		
		model.addAttribute("list", list);
		model.addAttribute("pi", pi);
		
		return "/admin/notice";
	}
	
	// 공지사항 수정
//	@PostMapping("admin/noticeEdit/{id}")
//	@PostMapping("admin/noticeEdit/{id}")
//	@ResponseBody
//	public String noticeEdit(@PathVariable("id") int noticeNo, @ModelAttribute Notice notice ) {
//		notice.setNoticeNo(noticeNo);
//		int result = aService.updateNotice(notice);
//		return result > 0 ? "success" : "fail";
//	}
	
			
//	 // 공지사항 목록 페이지 (사용자용)
//    @GetMapping("")
//    public String noticeList(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(required = false) String keyword,
//            Model model) {
//        
//        int listCount = noticeService.getNoticeCount();
//        PageInfo pi = Pagination.getPageInfo(page, listCount, 10);
//        
//        ArrayList<Notice> list = noticeService.selectNoticeList(pi);
//        ArrayList<Notice> latestNotices = noticeService.selectLatestNotices();
//        
//        model.addAttribute("list", list);
//        model.addAttribute("latestNotices", latestNotices);
//        model.addAttribute("pi", pi);
//        model.addAttribute("keyword", keyword);
//        
//        return "notice/list";
//    }
//    
    // 공지사항 상세 페이지 (사용자용)
//    @GetMapping("/{noticeNo}")
//    public String noticeDetail(@PathVariable int noticeNo, Model model) {
//        Notice notice = aService.selectNotice(noticeNo);
//        
//        if (notice == null) {
//            return "redirect:/notice";
//        }
//        
////        ArrayList<Notice> latestNotices = aService.selectLatestNotices();
//        
//        // 현재 공지사항은 제외
////        latestNotices.removeIf(n -> n.getNoticeNo() == noticeNo);
//        
//        model.addAttribute("notice", notice);
////        model.addAttribute("latestNotices", latestNotices);
//        
//        return "notice/detail";
//    }

	

	
	// 공지사항 작성 뷰페이지 
	@GetMapping("admin/noticeWrite") 
	public String noticeWriteView() {
	   return "/admin/noticeWrite";
	}
	// 공지사항 작성 처리
//	@PostMapping("admin/noticeWrite")
//	public String noticeWrite(@ModelAttribute Notice notice) {
//	    int result = aService.insertNotice(notice);
//	    return "redirect:/admin/notice";
//	}
	
	@PostMapping("admin/noticeWrite")
	public String noticeWrite(@ModelAttribute Notice notice) {
	    // 서버에서 현재 날짜 설정
	    notice.setCreateDate(new Date());
	    
	    int result = aService.insertNotice(notice);
	    return "redirect:/admin/notice";
	}
	
	// 공지사항 수정 페이지
	@GetMapping("admin/noticeEdit/{id}") 
	public String noticeEditView(@PathVariable("id") int noticeNo, Model model) {
	    Notice notice = aService.selectNotice(noticeNo);
	    model.addAttribute("notice", notice);
	    return "/admin/noticeEdit";
	}
	
	// 공지사항 수정 처리
	@PostMapping("admin/noticeEdit/{id}")
	@ResponseBody
	public HashMap<String, Boolean> noticeEdit(@PathVariable("id") int noticeNo, @ModelAttribute Notice notice) {
	    notice.setNoticeNo(noticeNo);
	    boolean success = aService.updateNotice(notice);
	    HashMap<String, Boolean> map = new HashMap<>();
	    map.put("success", success);
	    return map;
	}
	
	// 공지사항 삭제
	@PostMapping("admin/deleteNotice")
	@ResponseBody
	public HashMap<String, Boolean> deleteNotice(@RequestParam("noticeNo") int noticeNo) {
	    boolean success = aService.deleteNotice(noticeNo);
	    HashMap<String, Boolean> map = new HashMap<>();
	    map.put("success", success);
	    return map;
	}	
		
//	}
//	@PostMapping("admin/status")
//	@ResponseBody
//	public String updateStatus(@ModelAttribute Report report,@RequestParam("reportNo") int reportNo, @RequestParam("reportStatus")String reportStatus) {
//		report.setReportNo(reportNo);
//		report.setReportStatus(reportStatus);
//		return aService.updateStatus(report);
//	}

	@GetMapping("/admin/getCommNoByReplyNo")
	@ResponseBody
	public Map<String, Object> getCommNoByReplyNo(@RequestParam("replyNo") int replyNo) {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        // 댓글이 속한 게시글 번호 조회
	        int commNo = aService.getCommNoByReplyNo(replyNo);
	        
	        if (commNo > 0) {
	            response.put("success", true);
	            response.put("commNo", commNo);
	        } else {
	            response.put("success", false);
	            response.put("message", "댓글 정보를 찾을 수 없습니다.");
	        }
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
	    }
	    
	    return response;
	}
	
//	@GetMapping("admin/accessStats")
//	public String accessStats(
//	        @RequestParam(value = "startDate", required = false) String startDate,
//	        @RequestParam(value = "endDate", required = false) String endDate,
//	        @RequestParam(value = "loginStatus", required = false, defaultValue = "all") String loginStatus,
//	        @RequestParam(value = "insertTestData", required = false, defaultValue = "false") boolean insertTestData,
//	        Model model) {
//	    
//	    try {
////	        // 테스트 데이터 삽입 요청이 있으면 테스트 데이터 생성
////	        if (insertTestData) {
////	            aService.insertTestData();
////	        }
//	        
//	        // 날짜가 없으면 기본값 설정 (최근 7일)
//	        if (startDate == null || endDate == null) {
//	            LocalDate today = LocalDate.now();
//	            LocalDate weekAgo = today.minusDays(6);
//	            
//	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//	            startDate = weekAgo.format(formatter);
//	            endDate = today.format(formatter);
//	        }
//	        
//	        System.out.println("조회 기간: " + startDate + " ~ " + endDate + ", 로그인 상태: " + loginStatus);
//	        
//	        // 요약 통계 데이터
//	        int todayVisitors = aService.getTodayVisitors();
//	        int weeklyVisitors = aService.getWeeklyVisitors();
//	        int monthlyVisitors = aService.getMonthlyVisitors();
//	        int totalVisitors = aService.getTotalVisitors();
//	        
//	        // 차트 데이터
//	        ArrayList<HashMap<String, Object>> weeklyData = aService.getWeeklyData(startDate, endDate, loginStatus);
//	        ArrayList<HashMap<String, Object>> hourlyData = aService.getHourlyData(startDate, endDate, loginStatus);
//	        HashMap<String, Object> loginStatusData = aService.getLoginStatusData(startDate, endDate);
//	        
//	        // 비로그인 사용자 통계
//	        HashMap<String, Object> visitorReturnData = aService.getVisitorReturnRate(startDate, endDate);
////	        ArrayList<HashMap<String, Object>> popularPages = aService.getPopularPagesForNonLoggedUsers(startDate, endDate);
//	        
//	        // 월별 접속자 통계
//	        ArrayList<DailyStats> monthlyStats = aService.getMonthlyStats(startDate, endDate, loginStatus);
//	        
//	        // 데이터 로깅 (디버깅용)
//	        System.out.println("Today Visitors: " + todayVisitors);
//	        System.out.println("Weekly Visitors: " + weeklyVisitors);
//	        System.out.println("Monthly Visitors: " + monthlyVisitors);
//	        System.out.println("Total Visitors: " + totalVisitors);
//	        System.out.println("Weekly Data: " + weeklyData);
//	        System.out.println("Hourly Data: " + hourlyData);
//	        System.out.println("Login Status Data: " + loginStatusData);
//	        System.out.println("Visitor Return Data: " + visitorReturnData);
//	        System.out.println("Monthly Stats: " + monthlyStats);
//	        
//	        // 모델에 데이터 추가
//	        model.addAttribute("startDate", startDate);
//	        model.addAttribute("endDate", endDate);
//	        model.addAttribute("loginStatus", loginStatus);
////	          endDate);
//	        model.addAttribute("loginStatus", loginStatus);
//	        
//	        model.addAttribute("todayVisitors", todayVisitors);
//	        model.addAttribute("weeklyVisitors", weeklyVisitors);
//	        model.addAttribute("monthlyVisitors", monthlyVisitors);
//	        model.addAttribute("totalVisitors", totalVisitors);
//	        
//	        model.addAttribute("weeklyData", weeklyData != null ? weeklyData : new ArrayList<>());
//	        model.addAttribute("hourlyData", hourlyData != null ? hourlyData : new ArrayList<>());
//	        model.addAttribute("loginStatusData", loginStatusData != null ? loginStatusData : new HashMap<>());
//	        
//	        model.addAttribute("visitorReturnData", visitorReturnData != null ? visitorReturnData : new HashMap<>());
////	        model.addAttribute("popularPages", popularPages != null ? popularPages : new ArrayList<>());
//	        
//	        model.addAttribute("monthlyStats", monthlyStats != null ? monthlyStats : new ArrayList<>());
//	        
//	    } catch (Exception e) {
//	        System.err.println("접속 통계 조회 중 오류 발생: " + e.getMessage());
//	        e.printStackTrace();
//	        
//	        // 오류 발생 시 빈 데이터로 초기화
//	        model.addAttribute("startDate", startDate);
//	        model.addAttribute("endDate", endDate);
//	        model.addAttribute("loginStatus", loginStatus);
//	        model.addAttribute("todayVisitors", 0);
//	        model.addAttribute("weeklyVisitors", 0);
//	        model.addAttribute("monthlyVisitors", 0);
//	        model.addAttribute("totalVisitors", 0);
//	        model.addAttribute("weeklyData", new ArrayList<>());
//	        model.addAttribute("hourlyData", new ArrayList<>());
//	        model.addAttribute("loginStatusData", new HashMap<>());
//	        model.addAttribute("visitorReturnData", new HashMap<>());
//	        model.addAttribute("popularPages", new ArrayList<>());
//	        model.addAttribute("monthlyStats", new ArrayList<>());
//	        model.addAttribute("errorMessage", "통계 데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
//	    }
//	    
//	    return "admin/accessStats";
//	}




	
	
//	@GetMapping("admin/accessStats") 
//	public String accessStats() {
//		return "/admin/accessStats";
//	}
	@GetMapping("admin/payment")
	public String payment() {
		return "/admin/payment";
	}
	@GetMapping("/admin/reservation")
	public String reservation() {
		return "/admin/reservation";
	}
}