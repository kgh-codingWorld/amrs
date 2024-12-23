package com.application.amrs.comment;

import java.util.Date;
import java.util.Map;

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
@RequestMapping("/comment")
public class CommentRestController {

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private ReplyCommentService replyCommentService;
    
    @PostMapping("/registerComment/{blogId}")
    public ResponseEntity<CommentDTO> registerComment(@PathVariable("blogId") int blogId, @RequestBody CommentDTO commentDTO, HttpSession session) {
        try {
            // blogId를 CommentDTO에 설정 (필요한 경우)
            commentDTO.setBlogId(blogId);

            String loggedInMemberId = (String) session.getAttribute("memberId");
            if (loggedInMemberId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String memberNm = memberService.getMemberNameById(loggedInMemberId);
            if(memberNm != null) {
            	commentDTO.setMemberNm(memberNm);
            }
            commentDTO.setCreateDt(new Date());
            
            // 댓글을 등록하고, 등록된 댓글 정보를 반환
            CommentDTO savedComment = commentService.registerComment(commentDTO);
            CommentDTO returnComment = commentService.getOneComment(savedComment.getCommentId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(returnComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PostMapping("/modifyComment/{commentId}")
    public ResponseEntity<?> modifyComment(@PathVariable("commentId") int commentId, @RequestBody CommentDTO commentDTO) {
    	commentDTO.setCommentId(commentId);
        commentService.modifyComment(commentDTO);
        
        return ResponseEntity.ok().body("댓글이 성공적으로 수정되었습니다.");
    }
    
    @PostMapping("/removeComment/{commentId}")
    @ResponseBody
    public ResponseEntity<?> removeComment(@PathVariable("commentId") int commentId) {
    	commentService.removeComment(commentId);
    	return ResponseEntity.ok().body("댓글이 성공적으로 삭제되었습니다.");
    }
    
    @PostMapping("/registerReply/{commentId}")
    public ResponseEntity<ReplyCommentDTO> registerReply(@PathVariable("commentId") int commentId, @RequestBody ReplyCommentDTO replyCommentDTO, HttpSession session) {
		try {
			replyCommentDTO.setCommentId(commentId);
			
			String loggedInMemberId = (String) session.getAttribute("memberId");
			if(loggedInMemberId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
			}
			
			String memberNm = memberService.getMemberNameById(loggedInMemberId);
			if(memberNm != null) {
				replyCommentDTO.setMemberNm(memberNm);
			}
			replyCommentDTO.setCreateDt(new Date());
			
			ReplyCommentDTO savedReply = replyCommentService.registerReply(replyCommentDTO);
			ReplyCommentDTO returnReply = replyCommentService.getOneReply(savedReply.getReplyId());
			System.out.println("EEEEE" + savedReply.getReplyId());
			
			return ResponseEntity.status(HttpStatus.CREATED).body(returnReply);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
    }
    
    @PostMapping("/modifyReply/{replyId}")
    public ResponseEntity<?> modifyReply(@PathVariable("replyId") int replyId, @RequestBody ReplyCommentDTO replyCommentDTO){
    	replyCommentDTO.setReplyId(replyId);
    	replyCommentService.modifyReply(replyCommentDTO);
    	return ResponseEntity.ok().body("대댓글이 성공적으로 수정되었습니다.");
    }
    
    @PostMapping("/removeReply/{replyId}")
    public ResponseEntity<?> removeReply(@PathVariable("replyId") int replyId) {
    	System.out.println("^^" + replyId);
    	replyCommentService.removeReply(replyId);
    	return ResponseEntity.ok().body("대댓글이 성공적으로 삭제되었습니다.");
    }
}
