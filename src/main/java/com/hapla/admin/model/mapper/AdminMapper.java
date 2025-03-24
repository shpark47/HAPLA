package com.hapla.admin.model.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;


import com.hapla.admin.model.vo.AdminUsers;
import com.hapla.admin.model.vo.DailyStats;
import com.hapla.admin.model.vo.Notice;
import com.hapla.admin.model.vo.Report;
import com.hapla.comm.model.vo.Comm;
import com.hapla.comm.model.vo.Reply;
import com.hapla.review.model.vo.Review;
import com.hapla.users.model.vo.Users;

@Mapper
public interface AdminMapper {

	int userListCount(String keyword);

	ArrayList<AdminUsers> selectUserList(HashMap<String, Object> list, RowBounds rowBounds);

	int totalUsersCount();

	int totalComm();

	int totalReplyCount();

	int totalReview();

	int commListCount(HashMap<String, Object> map);

	int replyListCount(HashMap<String, Object> map);

	int reviewListCount(HashMap<String, Object> map);

	ArrayList<Comm> selectComm(HashMap<String, Object> map, RowBounds rowBounds);

	ArrayList<Reply> selectReply(HashMap<String, Object> map, RowBounds rowBounds);

	ArrayList<Review> selectReview(HashMap<String, Object> map, RowBounds rowBounds);

	int deleteComm(int commNo);

	int deleteReview(int reviewNo);

	int deleteReply(int replyNo);

	int totalWait();

	int totalAccept();

	int totalReject();

	int reportListCount();

	int noticeCount();

	ArrayList<Notice> selectNoticeList(HashMap<String, Object> map, RowBounds rowBounds);

	int insertNotice(Notice notice);

	Notice selectNotice(int noticeNo);

	int updateViews(int noticeNo);

	int updateNotice(Notice notice);

	int deleteNotice(int noticeNo);

	ArrayList<Report> selectReportList(HashMap<String, Object> map, RowBounds rowBounds);

	int updateStatus(Report report);

	int getCommNo(int reportNo);

	void deleteCommReport(int commNo);

	int updateMember(Users user);
	
	// 요약 통계
	int getTodayVisitors();

	int getWeeklyVisitors();

	int getMonthlyVisitors();

	int getTotalVisitors();

	// 일주일 접속자 통계
	ArrayList<HashMap<String, Object>> getWeeklyData(HashMap<String, Object> map);
	// 시간대별 접속자 통계
	ArrayList<HashMap<String, Object>> getHourlyData(HashMap<String, Object> map);
	// 로그인/비로그인 사용자 비율
	HashMap<String, Object> getLoginStatusData(HashMap<String, Object> map);
	// 비로그인 사용자 재방문율 조회
	HashMap<String, Object> getVisitorReturnRate(HashMap<String, Object> map);
	// 비로그인 사용자 인기 페이지 조회
//	ArrayList<HashMap<String, Object>> getPopularPagesForNonLoggedUsers(HashMap<String, Object> map);
	// 월별 접속자 통계
	ArrayList<DailyStats> getMonthlyStats(HashMap<String, Object> map);

	
	// 최신 공지사항 3개 조회
	ArrayList<Notice> selectRecent();

	int getReplyNo(int reportNo);

	void deleteReplyReport(int replyNo);

	int getCommNoByReplyNo(int replyNo);

}
