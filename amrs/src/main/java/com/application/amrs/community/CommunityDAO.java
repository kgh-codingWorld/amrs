package com.application.amrs.community;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface CommunityDAO {

	public List<Map<String, Object>> selectCommunityList();						// 게시글 전체조회
	public Map<String, Object> selectCommunityById(int communityId);				// 게시글 상세조회
	public void insertCommunity(CommunityDTO communityDTO);								// 게시글 등록
	public void updateCommunity(CommunityDTO communityDTO);								// 게시글 수정
	public void deleteCommunity(int communityId);									// 게시글 삭제
	public List<Map<String, Object>> selectMyCommunityList(String memberId);	// 내가 쓴 게시글 전체조회
	public void updateReadCnt(int communityId);									// 조회수 수정
	public void updateLikeCount(CommunityDTO communityDTO);							// 좋아요 수 수정
	public boolean checkMemberLike(Map<String, Object> params);				// 좋아요 회원 조회
	public void insertLike(Map<String, Object> params);						// 좋아요 등록
	public void deleteLike(Map<String, Object> params);						// 좋아요 취소
	public int getLikeCount(int communityId);									// 좋아요 수 조회
	public int countLikesForCommunity(int communityId);								// 최신 좋아요 수 조회
}
