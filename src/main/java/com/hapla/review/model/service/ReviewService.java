package com.hapla.review.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import com.hapla.common.PageInfo;
import com.hapla.review.model.mapper.ReviewMapper;
import com.hapla.review.model.vo.Review;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewMapper mapper;
	public int getListCount() {
		return mapper.getListCount();
	}
	public ArrayList<Review> selectReviewList(PageInfo pi) {
		int offset = (pi.getCurrentPage() -1) * pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return mapper.selectReviewList(rowBounds);
	}
	public Review selectReview(int reviewNo) {
		return mapper.selectReview(reviewNo);
	}
	public int insertReview(Review r) {
		return mapper.insertReview(r);
	}

}
