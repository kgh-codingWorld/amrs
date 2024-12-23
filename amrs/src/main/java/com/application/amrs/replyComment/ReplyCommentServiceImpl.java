package com.application.amrs.replyComment;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplyCommentServiceImpl implements ReplyCommentService {

	@Autowired
	private ReplyCommentDAO replyCommentDAO;
	
	@Override
	public ReplyCommentDTO registerReply(ReplyCommentDTO replyCommentDTO) {
		replyCommentDAO.insertReply(replyCommentDTO);
		return replyCommentDTO;
	}

	@Override
	public List<Map<String, Object>> getReplyCommentList(int commentId) {
		return replyCommentDAO.selectReplyList(commentId);
	}

	@Override
	public void modifyReply(ReplyCommentDTO replyCommentDTO) {
		replyCommentDAO.updateReply(replyCommentDTO);
	}
	
	@Override
	public void removeReply(int replyId) {
		replyCommentDAO.deleteReply(replyId);
	}

	@Override
	public ReplyCommentDTO getOneReply(int replyId) {
		return replyCommentDAO.selectOneReply(replyId);
	}

}
