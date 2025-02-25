package com.hapla.admin.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import com.hapla.admin.model.mapper.AdminMapper;
import com.hapla.admin.model.vo.AdminUsers;
import com.hapla.comm.model.vo.Comm;
import com.hapla.common.PageInfo;
import com.hapla.users.model.vo.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class AdminService {
	
	private final AdminMapper mapper;
	
		public int userListCount(String keyword) {
		return mapper.userListCount(keyword);
	}

		public ArrayList<Users> selectUserList(PageInfo userPi, String keyword) {
			int offset = (userPi.getCurrentPage() -1 ) * userPi.getBoardLimit();
			RowBounds rowBounds = new RowBounds(offset, userPi.getBoardLimit());
			return mapper.selectUserList(keyword, rowBounds);
		}
		
		public int totalComm(String keyword) {
			return mapper.totalComm(keyword);
		}

		public int totalReview(String keyword) {
			return mapper.totalReview(keyword);
		}

		public int userWrite(String keyword) {
			return mapper.userWrite(keyword);
		}

		public ArrayList<AdminUsers> selectUserWriteList(PageInfo writePi, String keyword) {
			int offset = (writePi.getCurrentPage() -1 ) * writePi.getBoardLimit();
			RowBounds rowBounds = new RowBounds(offset, writePi.getBoardLimit());
			return mapper.selectUserWriteList(keyword,rowBounds);
		}

		public int userReportCount(String keyword) {
			return mapper.userReportCount(keyword);
		}

		public ArrayList<AdminUsers> selectCommList(PageInfo pi, String keyword) {
			int offset = (pi.getCurrentPage() -1 ) * pi.getBoardLimit();
			RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
			return mapper.selectCommList(keyword,rowBounds);
		}

	

	

}
