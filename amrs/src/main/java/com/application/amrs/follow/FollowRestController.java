package com.application.amrs.follow;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.amrs.member.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowRestController {

	private final FollowService followService;
    private final MemberService memberService;

    // 팔로우 추가
    @PostMapping("/{friendName}")
    public ResponseEntity<String> follow(Authentication authentication, @PathVariable("friendName") String friendName) {
        String fromMemberId = memberService.getMemberId(authentication.getName()); // 로그인된 사용자
        String frommemberId = memberService.getMemberId(friendName); // 팔로우할 사용자

        followService.follow(fromMemberId, frommemberId);
        return ResponseEntity.ok("팔로우 성공");
    }

    // 팔로우 취소
    @DeleteMapping("/{friendName}")
    public ResponseEntity<String> unfollow(Authentication authentication, @PathVariable("friendName") String friendName) {
        String fromMemberId = memberService.getMemberId(authentication.getName()); // 로그인된 사용자
        String frommemberId = memberService.getMemberId(friendName); // 팔로우 취소할 사용자

        followService.cancelFollow(fromMemberId, frommemberId);
        return ResponseEntity.ok("팔로우 취소 성공");
    }

    // 팔로잉 목록 조회
    @GetMapping("/following")
    public ResponseEntity<List<FollowDTO>> getFollowingList(Authentication authentication) {
    	String memberId = memberService.getMemberId(authentication.getName()); // 로그인된 사용자

        List<FollowDTO> followingList = followService.getFollowingList(memberId);
        return ResponseEntity.ok(followingList);
    }

    // 팔로워 목록 조회
    @GetMapping("/followers")
    public ResponseEntity<List<FollowDTO>> getFollowersList(Authentication authentication) {
    	String memberId = memberService.getMemberId(authentication.getName()); // 로그인된 사용자

        List<FollowDTO> followersList = followService.getFollowersList(memberId);
        return ResponseEntity.ok(followersList);
    }
}
