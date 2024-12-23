package com.application.amrs.blog;

import java.util.List;
import java.util.Map;

public interface BlogService {

	public void registerBlog(BlogDTO blogDTO);
	public List<Map<String, Object>> getBlogList();
	public Map<String, Object> getBlogById(int blogId, boolean isIncreaseReadCnt);
	public void likePost(int blogId, int likeCount);
	public boolean hasMemberLikedPost(String memberId, int blogId);
	public int toggleLike(String memberId, int blogId, boolean liked);
	public void modifyBlog(BlogDTO blogDTO);
	public void removeBlog(int blogId);
	public List<Map<String, Object>> getMyBlogList(String memberId);
}
