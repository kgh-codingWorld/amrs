package com.application.amrs.replyComment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReplyCommentDAO {

	public void insertReply(ReplyCommentDTO replyCommentDTO);
	public ReplyCommentDTO selectOneReply(int replyId);
	public List<Map<String, Object>> selectReplyList(int commentId);
	public void updateReply(ReplyCommentDTO replyCommentDTO);
	public void deleteReply(int replyId);
}
