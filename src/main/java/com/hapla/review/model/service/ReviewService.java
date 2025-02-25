package com.hapla.review.model.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.hapla.comm.model.vo.Comm;
import com.hapla.common.PageInfo;
import com.hapla.review.model.mapper.ReviewMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewMapper mapper;
	public int getListCount(int i) {
		return mapper.getListCount(i);
	}
	public ArrayList<Comm> selectReviewList(PageInfo pi, int i) {
		return mapper.selectReviewList(pi, i);
	}

}
