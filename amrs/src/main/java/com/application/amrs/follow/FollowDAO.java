package com.application.amrs.follow;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FollowDAO {

	// 팔로우 추가
    void insertFollow(@Param("fromMemberId") String fromMemberId, @Param("toMemberId") String toMemberId);

    // 팔로우 취소
    void deleteFollow(@Param("fromMemberId") String fromMemberId, @Param("toMemberId") String toMemberId);

    // 팔로우 중복 확인
    FollowDTO findFollow(@Param("fromMemberId") String fromMemberId, @Param("toMemberId") String toMemberId);
    
    // 특정 사용자의 팔로잉 목록 조회
    List<FollowDTO> getFollowingList(@Param("fromMemberId") String fromMemberId);
    
    // 특정 사용자의 팔로워 목록 조회
    List<FollowDTO> getFollowersList(@Param("toMemberId") String toMemberId);
}
