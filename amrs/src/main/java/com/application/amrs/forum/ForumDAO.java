package com.application.amrs.forum;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface ForumDAO {

	public void insertForum(ForumDTO forumDTO);
	public List<Map<String, Object>> selectForumList();
	public Map<String, Object> selectForumById(int forumId);
	public void updateLikeCount(ForumDTO forumDTO);
	public boolean checkMemberLike(Map<String, Object> params);
	public void updateLikeStatus(Map<String, Object> params);
	public void insertLike(Map<String, Object> params);
	public void updateForum(ForumDTO forumDTO);
	public void updateReadCnt(int forumId);
	public void deleteForum(int forumId);
	public int getLikeCount(int forumId);
	public List<Map<String, Object>> selectMyForumList(String memberId);
	public int countLikesForForum(int forumId);
}
