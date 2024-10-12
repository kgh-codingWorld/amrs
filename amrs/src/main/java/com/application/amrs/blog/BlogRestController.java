package com.application.amrs.blog;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blog")
public class BlogRestController {

	@Autowired
	private BlogService blogService;
	
	@PostMapping("/likeCount/{blogId}")
	public ResponseEntity<?> likeCount(@PathVariable("blogId") int blogId, @RequestBody Map<String, Object> likeData) {
		boolean isLiked = (boolean) likeData.get("isLiked");
		int likeCount = (int) likeData.get("likeCount");
		
		blogService.likePost(blogId, likeCount);
		System.out.println("blogId: " + blogId + ", likeCount: " + likeCount + ", isLiked: " + isLiked);
		
		Map<String, Object> response = new HashMap<>();
		response.put("updatedLikeCount", likeCount);
		return ResponseEntity.ok(response);
	}
}
