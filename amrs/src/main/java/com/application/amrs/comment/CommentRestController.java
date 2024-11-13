package com.application.amrs.comment;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.amrs.member.MemberDTO;
import com.application.amrs.member.MemberService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/comment")
public class CommentRestController {

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private MemberService memberService;
    
    @PostMapping("/registerComment/{forumId}")
    public ResponseEntity<CommentDTO> registerComment(@PathVariable("forumId") int forumId, @RequestBody CommentDTO commentDTO, HttpSession session) {
        try {
            // forumId를 CommentDTO에 설정 (필요한 경우)
            commentDTO.setForumId(forumId);

            String loggedInMemberId = (String) session.getAttribute("memberId");
            if (loggedInMemberId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String memberNm = memberService.getMemberNameById(loggedInMemberId);
            if(memberNm != null) {
            	commentDTO.setMemberNm(memberNm);
            }
            commentDTO.setMemberNm(memberNm);
            commentDTO.setCreateDt(new Date());
            
            // 댓글을 등록하고, 등록된 댓글 정보를 반환
            CommentDTO savedComment = commentService.registerComment(commentDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
