package com.hapla.review.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.hapla.review.model.vo.Review;

@Mapper
public interface ReviewMapper {

	int getListCount();

	ArrayList<Review> selectReviewList(RowBounds rowBounds);

	Review selectReview(int reviewNo);

	int insertReview(Review r);

	
}
