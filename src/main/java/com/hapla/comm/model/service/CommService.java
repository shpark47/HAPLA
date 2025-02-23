package com.hapla.comm.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import com.hapla.comm.model.mapper.CommMapper;
import com.hapla.comm.model.vo.Comm;
import com.hapla.common.PageInfo;

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
}
