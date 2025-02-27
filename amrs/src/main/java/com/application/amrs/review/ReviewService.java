package com.application.amrs.review;

public interface ReviewService {
	
	public Integer getReviewIdByPaymentId(int paymentId);
	public void registerReview(int paymentId, String memberId, String reviewContent);
	public ReviewDTO getReviewDetail(int paymentId, int reviewId);
	public boolean modifyReview(ReviewDTO reviewDTO);
	public String removeReview(int reviewId);
}
