package com.application.amrs.comment;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.amrs.member.MemberDTO;
import com.application.amrs.member.MemberService;
import com.application.amrs.replyComment.ReplyCommentDTO;
import com.application.amrs.replyComment.ReplyCommentService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/comment/api")
public class CommentRestController {

	private static final Logger logger = LoggerFactory.getLogger(CommentRestController.class);
	
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private ReplyCommentService replyCommentService;
    
    @PostMapping("/registerComment/{communityId}")
    public ResponseEntity<?> registerComment(@PathVariable("communityId") int communityId, @RequestBody CommentDTO commentDTO, HttpSession session) {
    	return processCommentRegistration(communityId, commentDTO, session);
    }
    
    @PostMapping("/modifyComment/{commentId}")
    public ResponseEntity<?> modifyComment(@PathVariable int commentId, @RequestBody CommentDTO commentDTO) {
        commentDTO.setCommentId(commentId);
        commentService.modifyComment(commentDTO);
        return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
    }

    @PostMapping("/removeComment/{commentId}")
    public ResponseEntity<?> removeComment(@PathVariable int commentId) {
        commentService.removeComment(commentId);
        return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/registerReply/{commentId}")
    public ResponseEntity<?> registerReply(@PathVariable int commentId, @RequestBody ReplyCommentDTO replyCommentDTO, HttpSession session) {
        return processReplyRegistration(commentId, replyCommentDTO, session);
    }

    @PostMapping("/modifyReply/{replyId}")
    public ResponseEntity<?> modifyReply(@PathVariable int replyId, @RequestBody ReplyCommentDTO replyCommentDTO) {
        replyCommentDTO.setReplyId(replyId);
        replyCommentService.modifyReply(replyCommentDTO);
        return ResponseEntity.ok("대댓글이 성공적으로 수정되었습니다.");
    }

    @PostMapping("/removeReply/{replyId}")
    public ResponseEntity<?> removeReply(@PathVariable int replyId) {
        replyCommentService.removeReply(replyId);
        return ResponseEntity.ok("대댓글이 성공적으로 삭제되었습니다.");
    }

    // 댓글 등록 처리
    private ResponseEntity<?> processCommentRegistration(int communityId, CommentDTO commentDTO, HttpSession session) {
        try {
            String memberId = getSessionMemberId(session);
            if (memberId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");

            commentDTO.setCommunityId(communityId);
            commentDTO.setMemberNm(getMemberNameById(memberId));
            commentDTO.setCreateDt(new Date());

            CommentDTO savedComment = commentService.registerComment(commentDTO);
            CommentDTO returnComment = commentService.getOneComment(savedComment.getCommentId());
            returnComment.setMemberNm(memberService.maskLastCharacter(returnComment.getMemberNm()));

            return ResponseEntity.status(HttpStatus.CREATED).body(returnComment);
        } catch (Exception e) {
            return handleException(e, "댓글 등록 중 오류 발생");
        }
    }

    // 대댓글 등록 처리
    private ResponseEntity<?> processReplyRegistration(int commentId, ReplyCommentDTO replyCommentDTO, HttpSession session) {
        try {
            String memberId = getSessionMemberId(session);
            if (memberId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");

            replyCommentDTO.setCommentId(commentId);
            replyCommentDTO.setMemberNm(getMemberNameById(memberId));
            replyCommentDTO.setCreateDt(new Date());

            ReplyCommentDTO savedReply = replyCommentService.registerReply(replyCommentDTO);
            ReplyCommentDTO returnReply = replyCommentService.getOneReply(savedReply.getReplyId());
            returnReply.setMemberNm(memberService.maskLastCharacter(returnReply.getMemberNm()));

            return ResponseEntity.status(HttpStatus.CREATED).body(returnReply);
        } catch (Exception e) {
            return handleException(e, "대댓글 등록 중 오류 발생");
        }
    }

    // 세션에서 memberId 가져오기
    private String getSessionMemberId(HttpSession session) {
        return (String) session.getAttribute("memberId");
    }

    // memberId로 회원 이름 가져오기
    private String getMemberNameById(String memberId) {
        return memberService.getMemberNameById(memberId);
    }

    // 예외 처리 메서드
    private ResponseEntity<?> handleException(Exception e, String errorMessage) {
        logger.error(errorMessage, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }
}
