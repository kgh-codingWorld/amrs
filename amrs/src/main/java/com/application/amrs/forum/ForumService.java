package com.application.amrs.forum;

import java.util.List;
import java.util.Map;

public interface ForumService {

	public void registerForum(ForumDTO forumDTO);
	public List<Map<String, Object>> getForumList();
	public Map<String, Object> getForumById(int forumId, boolean isIncreaseReadCnt);
	public void likePost(int forumId, int likeCount);
	public boolean hasMemberLikedPost(String memberId, int forumId);
	public int toggleLike(String memberId, int forumId, boolean liked);
	public void modifyForum(ForumDTO forumDTO);
	public void removeForum(int forumId);
	public List<Map<String, Object>> getMyForumList(String memberId);
}
