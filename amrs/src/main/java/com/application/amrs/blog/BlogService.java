package com.application.amrs.blog;

import java.util.List;
import java.util.Map;

public interface BlogService {

	public void registerBlog(BlogDTO blogDTO);
	public List<Map<String, Object>> getBlogList();
	public Map<String, Object> getBlogById(int blogId, boolean isIncreaseReadCnt);
	public int likePost(int blogId, int likeCount);
	public void modifyBlog(BlogDTO blogDTO);
	public void removeBlog(int blogId);
}
