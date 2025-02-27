package com.application.amrs.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review/api")
public class ReviewRestController {

	@Autowired
	private ReviewService reviewService;
	
	@PostMapping("/removeReview")
	public ResponseEntity<String> removeReview(@RequestParam("reviewId") int reviewId) {
		System.out.println("restcontroller 확인: before");
		String result = reviewService.removeReview(reviewId);
		System.out.println("restcontroller 확인: after");
		if(result.equals("y")) {
			return ResponseEntity.ok(result);
		}
		return null; 
	}
}
