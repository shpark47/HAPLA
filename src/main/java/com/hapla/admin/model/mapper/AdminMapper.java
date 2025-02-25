package com.hapla.admin.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.hapla.admin.model.vo.AdminUsers;
import com.hapla.comm.model.vo.Comm;
import com.hapla.users.model.vo.Users;

@Mapper
public interface AdminMapper {

	int userListCount(String keyword);

	int totalComm(String keyword);

	int totalReview(String keyword);

	ArrayList<Users> selectUserList(String keyword, RowBounds rowBounds);

	int userWrite(String keyword);

	ArrayList<AdminUsers> selectUserWriteList(String keyword, RowBounds rowBounds);

	int userReportCount(String keyword);

	ArrayList<AdminUsers> selectCommList(String keyword, RowBounds rowBounds);



}
