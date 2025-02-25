package com.hapla.admin.model.vo;

import java.util.ArrayList;

import com.hapla.comm.model.vo.Comm;
import com.hapla.review.model.vo.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminUsers {
	private String nickname;
	private ArrayList<Comm> comm;
	private ArrayList<Review> review;
	
	private int commCount;
	private int reviewCount;
}
