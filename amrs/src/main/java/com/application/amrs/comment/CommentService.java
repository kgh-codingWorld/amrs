package com.application.amrs.comment;

import java.util.List;
import java.util.Map;

public interface CommentService {

	public CommentDTO registerComment(CommentDTO commentDTO);
	public CommentDTO getOneComment(int commentId);
	public List<Map<String, Object>> getCommentList(int blogId);
	public int countCommentByBlogId(int blogId);
	public void modifyComment(CommentDTO commentDTO);
	public void removeComment(int commentId);
}
