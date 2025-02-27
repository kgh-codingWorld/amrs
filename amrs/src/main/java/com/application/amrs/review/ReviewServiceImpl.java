package com.application.amrs.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired 
	private ReviewDAO reviewDAO;
	
	@Override
	public Integer getReviewIdByPaymentId(int paymentId) {
		return reviewDAO.findReviewIdByPaymentId(paymentId);
	}
	
	@Override
	public void registerReview(int paymentId, String memberId, String reviewContent) {
		reviewDAO.insertReview(paymentId, memberId, reviewContent);
	}

	@Override
	public ReviewDTO getReviewDetail(int paymentId, int reviewId) {
		System.out.println("reviewId in getReviewDetail before dao: " + reviewId);
		ReviewDTO review = reviewDAO.selectReviewDetail(paymentId, reviewId);
		System.out.println("reviewId in getReviewDetail after dao: " + review);
		return review;
	}

	@Override
	public boolean modifyReview(ReviewDTO reviewDTO) {
		System.out.println("reviewId: " + reviewDTO.getReviewId());
		return reviewDAO.updateReview(reviewDTO) > 0;
	}

	@Override
	public String removeReview(int reviewId) {
		System.out.println("service 확인: before");
		boolean result = reviewDAO.deleteReview(reviewId);
		System.out.println("service 확인: after");
		if(result) return "y";
		else return "n";
	}
}
