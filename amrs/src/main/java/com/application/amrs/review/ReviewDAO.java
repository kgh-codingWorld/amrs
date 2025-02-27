package com.application.amrs.review;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewDAO {

	public Integer findReviewIdByPaymentId(int paymentId);
	public void insertReview(@Param("paymentId") int paymentId, 
							 @Param("memberId") String memberId, 
							 @Param("reviewContent") String reviewContent);
	public ReviewDTO selectReviewDetail(@Param("paymentId") int paymentId, @Param("reviewId") int reviewId);
	public int updateReview(ReviewDTO reviewDTO);
	public boolean deleteReview(int reviewId);
}
