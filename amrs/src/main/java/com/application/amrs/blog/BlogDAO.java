package com.application.amrs.blog;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface BlogDAO {

	public List<Map<String, Object>> selectBlogList();						// 게시글 전체조회
	public Map<String, Object> selectBlogById(int BlogId);				// 게시글 상세조회
	public void insertBlog(BlogDTO BlogDTO);								// 게시글 등록
	public void updateBlog(BlogDTO BlogDTO);								// 게시글 수정
	public void deleteBlog(int BlogId);									// 게시글 삭제
	public List<Map<String, Object>> selectMyBlogList(String memberId);	// 내가 쓴 게시글 전체조회
	public void updateReadCnt(int BlogId);									// 조회수 수정
	public void updateLikeCount(BlogDTO BlogDTO);							// 좋아요 수 수정
	public boolean checkMemberLike(Map<String, Object> params);				// 좋아요 회원 조회
	public void insertLike(Map<String, Object> params);						// 좋아요 등록
	public void deleteLike(Map<String, Object> params);						// 좋아요 취소
	public int getLikeCount(int BlogId);									// 좋아요 수 조회
	public int countLikesForBlog(int BlogId);								// 최신 좋아요 수 조회
}
