package com.application.amrs.replyComment;

import java.util.List;
import java.util.Map;

public interface ReplyCommentService {

	public ReplyCommentDTO registerReply(ReplyCommentDTO replyCommentDTO);
	public ReplyCommentDTO getOneReply(int replyId);
	public List<Map<String, Object>> getReplyCommentList(int commentId);
	public void modifyReply(ReplyCommentDTO replyCommentDTO);
	public void removeReply(int replyId);
}
