package com.hapla.comm.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import com.hapla.comm.model.mapper.CommMapper;
import com.hapla.comm.model.vo.Comm;
import com.hapla.comm.model.vo.Reply;
import com.hapla.common.PageInfo;
import com.hapla.users.model.vo.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommService {
    private final CommMapper mapper;

	public int getListCount(int i) {
		return mapper.getListCount(i);
	}

	public ArrayList<Comm> selectCommList(PageInfo pi, int i) {
		int offset = (pi.getCurrentPage() -1) * pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return mapper.selectCommList(i, rowBounds);
	}

	public int insertComm(Comm c) {
		return mapper.insertComm(c);
	}

	public Comm selectComm(int commNo, String name) {
		Comm c = mapper.selectComm(commNo);
		if(c != null && name != null && !(c.getName().equals(name))) {
			int result = mapper.updateCount(commNo);
			if(result > 0) {
				c.setViews(c.getViews() + 1);
			}
		} return c;
	}

	public ArrayList<Reply> selectReplyList(int commNo) {
		return mapper.selectReplyList(commNo);
	}

	public int insertReply(Reply r) {
		return mapper.insertReply(r);
	}

	public int deleteComm(int commNo) {
		return mapper.deleteComm(commNo);
	}

	public int updateComm(Comm c) {
		return mapper.updateComm(c);
	}

	public int deleteReply(int replyNo) {
		return mapper.deleteReply(replyNo);
	}

	public int updateReply(Reply r) {
		return mapper.updateReply(r);
	}

	public int checkUserLike(int userNo, int commNo) {
		return mapper.checkUserLike(userNo, commNo);
	}

	public void addLike(int userNo, int commNo) {
        mapper.insertLike(userNo, commNo);
//        mapper.incrementLikeCount(commNo);
    }

    public void removeLike(int userNo, int commNo) {
        mapper.deleteLike(userNo, commNo);
//        mapper.decrementLikeCount(commNo);
    }

    public int getLikeCount(int commNo) {
        return mapper.countLikes(commNo);
    }
}
