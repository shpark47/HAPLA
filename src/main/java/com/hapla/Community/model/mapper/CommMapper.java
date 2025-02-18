package com.hapla.Community.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.hapla.Community.model.vo.Comm;
import com.hapla.Community.model.vo.PageInfo;

@Mapper
public interface CommMapper {

	int getListCount(int i);

	ArrayList<Comm> selectCommList(int i, RowBounds rowBounds);
}
