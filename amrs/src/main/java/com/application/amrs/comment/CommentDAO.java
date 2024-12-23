package com.application.amrs.comment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentDAO {

	public void insertComment(CommentDTO commentDTO);
	public CommentDTO selectOneComment(int commentId);
	public List<Map<String, Object>> selectCommentList(int blogId);
	public int countCommentByBlogId(int blogId);
	public void updateComment(CommentDTO commentDTO);
	public void deleteComment(int commentId);
}
