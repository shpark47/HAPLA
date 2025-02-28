package com.hapla.review.model.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hapla.comm.model.vo.Comm;
import com.hapla.common.PageInfo;
import com.hapla.review.model.vo.Review;

@Mapper
public interface ReviewMapper {

	int getListCount(int i);

	ArrayList<Review> selectReviewList(PageInfo pi, int i);

	Review selectReview(int reviewNo);

	int insertReview(Review r);

	
}
