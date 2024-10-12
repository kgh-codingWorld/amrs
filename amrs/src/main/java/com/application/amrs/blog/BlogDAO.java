package com.application.amrs.blog;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface BlogDAO {

	public void insertBlog(BlogDTO blogDTO);
	public List<Map<String, Object>> selectBlogList();
	public Map<String, Object> selectBlogById(int blogId);
	public void updateLikeCount(@Param("blogId") int blogId, @Param("likeCount") int likeCount);
	public void updateBlog(BlogDTO blogDTO);
	public void updateReadCnt(int blogId);
	public void deleteBlog(int blogId);
	public int getLikeCount(int blogId);
}
