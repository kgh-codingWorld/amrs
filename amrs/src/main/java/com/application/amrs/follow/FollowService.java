package com.application.amrs.follow;

import java.util.List;

public interface FollowService {

	// 팔로우 추가
    void follow(String fromMemberId, String toMemberId);

    // 팔로우 취소
    void cancelFollow(String fromMemberId, String toMemberId);

    // 팔로우 목록 조회 (팔로잉)
    List<FollowDTO> getFollowingList(String fromMemberId);

    // 팔로워 목록 조회 (팔로워)
    List<FollowDTO> getFollowersList(String toMemberId);
}
