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

import com.application.amrs.community.CommunityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {
	
	@Value("${file.repo.path}")
	private String fileRepositoryPath;

	@Value("${admin.id}")
	private String adminId;
	
	@Value("${admin.password}")
	private String adminPwd;
	
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
    public String login(@ModelAttribute MemberDTO memberDTO, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        if (isAdmin(memberDTO)) {
            session.setAttribute("adminId", adminId);
            session.setAttribute("adminNm", "관리자");
            
            System.out.println("adminId: " + adminId);
            System.out.println("memberDTO.getMemberId(): " + memberDTO.getMemberId());
            return "redirect:/admin/dashboard";
        }

        if (memberService.login(memberDTO)) {
            session.setAttribute("memberId", memberDTO.getMemberId());
            session.setAttribute("memberNm", memberDTO.getMemberNm());
            return "redirect:/";
        } else {
            model.addAttribute("failMsg", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "member/login"; // 로그인 실패 시 로그인 페이지로 돌아감
        }
    }
	
	
	/* 로그아웃 기능은 CommonController에 있음 */
	
	
	// 비밀번호 일치 여부
	@PostMapping("/isValidPasswd")
	@ResponseBody
	public String isValidPasswd(@RequestParam("passwd") String passwd, @RequestParam("memberId") String memberId) {
		
		String isValidPasswd = "n";
		//MemberDTO memberDTO = new MemberDTO();
		//memberDTO.setMemberId(memberId);
		
		if(memberService.isValidPasswd(passwd, memberId)) {
			isValidPasswd = "y";
		}
		
		System.out.println("#@@@@" + memberId);
		System.out.println("#@@@@" + isValidPasswd);
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
	
	// 관리자 여부 확인 메서드
    private boolean isAdmin(MemberDTO memberDTO) {
        return memberDTO.getMemberId().equals(adminId) && memberDTO.getPasswd().equals(adminPwd);
    }
}
