package com.application.amrs.review;

import java.util.Date;

import lombok.Data;

@Data
public class ReviewDTO {

	private int reviewId;
	private String reviewContent;
	private int paymentId;
	private String memberId;
	private Date createDt;
	private Date updateDt;
}
