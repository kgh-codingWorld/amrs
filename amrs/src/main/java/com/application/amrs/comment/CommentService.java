package com.application.amrs.comment;

import java.util.List;
import java.util.Map;

public interface CommentService {

	public CommentDTO registerComment(CommentDTO commentDTO);
	public List<Map<String, Object>> getCommentList(int forumId);
}
