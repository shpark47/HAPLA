package com.hapla.comm.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.hapla.comm.model.vo.Comm;

@Mapper
public interface CommMapper {

	int getListCount(int i);

	ArrayList<Comm> selectCommList(int i, RowBounds rowBounds);

	int insertComm(Comm c);
}
