package com.application.amrs.member;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {
	
	@Value("${file.repo.path}")
	private String fileRepositoryPath;
	
	@Autowired
	private MemberService memberService;
	
	// 회원가입
	@PostMapping("/registerMember")
	private String registerMember(@RequestParam("uploadProfile") MultipartFile uploadProfile, @ModelAttribute MemberDTO memberDTO) throws IllegalStateException, IOException {
		memberService.registerMember(uploadProfile, memberDTO);
		return "redirect:/member/login";
	}
	
	// 아이디 중복체크
	@PostMapping("/checkDuplicatedId")
	@ResponseBody
	public String checkDuplicatedId(@RequestParam("memberId") String memberId) {
		System.out.println("controller의 memberId: " + memberId);
		return memberService.checkDuplicatedId(memberId);
	}
	
	// 로그인
	@PostMapping("/login")
	public String login(@ModelAttribute MemberDTO memberDTO, HttpServletRequest request) {
		
		if(memberService.login(memberDTO)) {
			
			HttpSession session = request.getSession();
			session.setAttribute("memberId", memberDTO.getMemberId());
			session.setAttribute("memberNm", memberDTO.getMemberNm());
			
			System.out.println("memberDTO : " + memberDTO);
			System.out.println("memberId : " + memberDTO.getMemberId());
			System.out.println("passwd : " + memberDTO.getPasswd());
		}
		
		return "redirect:/";
	}
	
	
	/* 로그아웃 기능은 CommonController에 있음 */
	
	
	// 비밀번호 일치 여부
	@PostMapping("/isValidPasswd")
	@ResponseBody
	public String isValidPasswd(@RequestParam("passwd") String passwd, @RequestParam("memberId") String memberId) {
		
		String isValidPasswd = "n";
		MemberDTO memberDTO = new MemberDTO();
		memberDTO.setMemberId(memberId);
		
		if(memberService.isValidPasswd(passwd, memberDTO)) {
			isValidPasswd = "y";
		}
		return isValidPasswd;
	}

	// 비밀번호 수정
	@PostMapping("/modifyMyPasswd")
	public String modifyMyPasswd(@RequestParam("newPasswd") String newPasswd, @RequestParam("memberId") String memberId) {
		
		MemberDTO memberDTO = new MemberDTO();
		memberDTO.setMemberId(memberId);
		memberService.modifyMyPasswd(newPasswd, memberDTO);
		return "redirect:/member/myInfo";
	}
	
	// 개인정보 수정
	@PostMapping("/modifyMyInfo")
	public String modifyMyInfo(@RequestParam("uploadProfile") MultipartFile uploadProfile, @ModelAttribute MemberDTO memberDTO) throws IllegalStateException, IOException {
		memberService.modifyMyInfo(uploadProfile, memberDTO);
		return "redirect:/member/myInfo";
	}
	
	// 회원탈퇴
	@PostMapping("/removeAccount")
    public String removeAccount(@RequestParam("passwd") String passwd, 
                                @RequestParam("memberId") String memberId, 
                                Model model, HttpServletRequest request) {
		
        // 비밀번호 검증 및 탈퇴 처리
        boolean isRemoved = memberService.removeAccount(passwd, memberId);

        if (isRemoved) {
        	HttpSession session = request.getSession();
    		session.invalidate();
            // 탈퇴 성공 시 홈으로 리다이렉트
            return "redirect:/";
        } else {
            // 비밀번호가 틀린 경우
            model.addAttribute("error", "비밀번호가 올바르지 않습니다.");
            return "member/removeAccount"; // 다시 탈퇴 페이지로
        }
    }
	
}
