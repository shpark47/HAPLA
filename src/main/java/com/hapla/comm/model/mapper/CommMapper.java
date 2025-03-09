package com.hapla.comm.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.hapla.comm.model.vo.Comm;
import com.hapla.comm.model.vo.Reply;

@Mapper
public interface CommMapper {

	int getListCount(int i);

	ArrayList<Comm> selectCommList(int i, RowBounds rowBounds);

	int insertComm(Comm c);

	Comm selectComm(int commNo);

	int updateCount(int commNo);

	ArrayList<Reply> selectReplyList(int commNo);

	int insertReply(Reply r);

	int deleteComm(int commNo);

	int updateComm(Comm c);

	int deleteReply(int replyNo);

	int updateReply(Reply r);
}
