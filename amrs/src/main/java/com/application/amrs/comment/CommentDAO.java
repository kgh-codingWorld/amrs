package com.application.amrs.comment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentDAO {

	public void insertComment(CommentDTO commentDTO);
	public List<Map<String, Object>> selectCommentList(int forumId);
}
