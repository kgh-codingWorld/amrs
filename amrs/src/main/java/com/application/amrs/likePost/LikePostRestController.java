package com.application.amrs.likePost;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.amrs.community.CommunityService;

@RestController
@RequestMapping("/like")
public class LikePostRestController {

	@Autowired
	private CommunityService communityService;
	
	@PostMapping("/likeCount/{communityId}")
	public ResponseEntity<?> likeCount(@PathVariable("communityId") int communityId, @RequestBody Map<String, Object> likeData) {
		boolean isLiked = (boolean) likeData.get("isLiked");
		int likeCount = (int) likeData.get("likeCount");
		
		communityService.likePost(communityId, likeCount);
		
		Map<String, Object> response = new HashMap<>();
		response.put("updatedLikeCount", likeCount);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/toggleLike")
	public ResponseEntity<?> toggleLike(@RequestBody LikePostDTO likePostDTO) {
	    try {
	        String memberId = likePostDTO.getMemberId();
	        int communityId = likePostDTO.getCommunityId();
	        boolean liked = likePostDTO.isLiked();

	        // 좋아요 상태 업데이트 및 최신 LIKE_COUNT 반환
	        int updatedLikeCount = communityService.toggleLike(memberId, communityId, liked);

	        return ResponseEntity.ok().body(Map.of("likeCount", updatedLikeCount));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러");
	    }
	}

}
