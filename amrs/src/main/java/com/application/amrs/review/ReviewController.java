package com.application.amrs.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/review")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;
	
	@PostMapping("/registerReview")
	public String registerReview(@RequestParam("paymentId") int paymentId,
								 @RequestParam("memberId") String memberId, 
								 @RequestParam("reviewContent") String reviewContent) {
		reviewService.registerReview(paymentId, memberId, reviewContent);
		return "redirect:/member/mypageMain";
	}
	
	@PostMapping("/modifyReview")
	public String modifyReview(@ModelAttribute ReviewDTO reviewDTO) {
		reviewService.modifyReview(reviewDTO);
		return "redirect:/member/mypageMain";
	}
}
