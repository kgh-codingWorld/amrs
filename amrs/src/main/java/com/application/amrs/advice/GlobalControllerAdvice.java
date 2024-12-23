package com.application.amrs.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.application.amrs.member.MemberDTO;
import com.application.amrs.member.MemberService;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {

	@Autowired
	private MemberService memberService;
	
	@ModelAttribute
	public void addMemberProfileToModel(HttpSession session, Model model) {
        String memberId = (String) session.getAttribute("memberId");

        if (memberId != null) {
            // 사용자 정보 조회
            MemberDTO memberDTO = memberService.getMemberDetail(memberId);
            if (memberDTO != null) {
                model.addAttribute("memberDTO", memberDTO); // 사용자 DTO 전달
            }
        }
    }
}
