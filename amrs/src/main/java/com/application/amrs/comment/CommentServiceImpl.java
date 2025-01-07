package com.application.amrs.comment;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.amrs.member.MemberService;

@Service
public class CommentServiceImpl implements CommentService{

	@Autowired
	private CommentDAO commentDAO;
	
	@Autowired
	private MemberService memberService;
	
	@Override
	public CommentDTO registerComment(CommentDTO commentDTO) {
		commentDAO.insertComment(commentDTO);
		return commentDTO;
	}

	@Override
	public List<Map<String, Object>> getCommentList(int forumId) {
		
		List<Map<String, Object>> comments = commentDAO.selectCommentList(forumId);
		
		for(Map<String, Object> comment : comments) {
			String memberId = (String) comment.get("memberId");
			if(memberId != null) {
				String memberNm = memberService.getMemberNameById(memberId);
				String maskedMemberNm = memberService.maskLastCharacter(memberNm);
				if(memberNm != null) {
					comment.put("maskedMemberNm", maskedMemberNm);
				}
			}
		}
		
		return comments;
	}

	@Override
	public int countCommentByCommunityId(int forumId) {
		return commentDAO.countCommentByCommunityId(forumId);
	}

	@Override
	public void modifyComment(CommentDTO commentDTO) {
		commentDAO.updateComment(commentDTO);
	}

	@Override
	public void removeComment(int commentId) {
		commentDAO.deleteComment(commentId);
	}

	@Override
	public CommentDTO getOneComment(int commentId) {
		return commentDAO.selectOneComment(commentId);
	}

}
