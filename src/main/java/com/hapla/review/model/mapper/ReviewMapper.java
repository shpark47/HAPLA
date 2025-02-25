package com.hapla.review.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import com.hapla.comm.model.vo.Comm;
import com.hapla.common.PageInfo;

@Mapper
public interface ReviewMapper {

	int getListCount(int i);

	ArrayList<Comm> selectReviewList(PageInfo pi, int i);
	
}
