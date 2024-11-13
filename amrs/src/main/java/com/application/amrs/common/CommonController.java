package com.application.amrs.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.application.amrs.comment.CommentService;
import com.application.amrs.exhibition.ExhibitionRestController;
import com.application.amrs.exhibition.ExhibitionRestController.ExhibitionItem;
import com.application.amrs.forum.ForumService;
import com.application.amrs.member.MemberDTO;
import com.application.amrs.member.MemberService;
import com.application.amrs.payment.PaymentDTO;
import com.application.amrs.payment.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommonController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ForumService forumService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private PaymentService paymentService;
	
//	@Autowired
//    private ExhibitionRestController exhibitionRestController;
	
	/* 페이지 이동용 컨트롤러 */
	// .
	// .
	// .
	
	@GetMapping("/")
	public String main() {
		// 메인 화면으로 이동
		return "main/index";
	}
	
	/*회원 관련 메서드*/
	
	@GetMapping("/member/login")
	public String login() {
		// 로그인 페이지로 이동
		return "member/login";
	}
	
	@GetMapping("/member/findId")
	public String findAccount() {
		return "member/findId";
	}
	
	@GetMapping("/member/resetPassword")
	public String resetPassword() {
		return "member/resetPassword";
	}
	
	@GetMapping("/member/logout")
	public String logout(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		session.invalidate();
		
		return "redirect:/";
	}
	
	@GetMapping("/member/registerMember")
	public String registerMember() {
		// 회원가입 페이지로 이동
		return "member/registerMember";
	}

	@GetMapping("/member/mypageMain")
	public String mypageMain(HttpServletRequest request, Model model) {
		
		HttpSession session = request.getSession();
		String memberId = (String)session.getAttribute("memberId");
		
		if(memberId != null) {
			// 현재 로그인된 아이디로 payment 정보 호출
			List<Map<String, Object>> paymentList = paymentService.getPaymentList(memberId);
			model.addAttribute("paymentList", paymentList);
			
			
		} else {
			model.addAttribute("paymentList", Collections.emptyList());
		}
		model.addAttribute("memberId", memberId);
		
		// 마이페이지 메인으로 이동
		return "member/mypageMain";
	}
	
	@GetMapping("/member/checkPassword")
	public String checkPassword(HttpServletRequest request, Model model) {
		// 개인정보확인/수정 페이지 전 비밀번호 확인
		HttpSession session = request.getSession();
		model.addAttribute("memberDTO", memberService.getMemberDetail((String)session.getAttribute("memberId")));
		return "member/checkPassword";
	}
	
	@GetMapping("/member/myInfo")
	public String myInfo(HttpServletRequest request, Model model) {
		// 개인정보확인/수정 페이지로 이동
		HttpSession session = request.getSession();
		model.addAttribute("memberDTO", memberService.getMemberDetail((String)session.getAttribute("memberId")));
		return "member/myInfo";
	}
	
	@GetMapping("/member/removeAccount")
	public String removeAccount(Model model, HttpSession session) {
		
		String memberId = (String) session.getAttribute("memberId");
		model.addAttribute("memberDTO", memberService.getMemberDetail(memberId));
		//MemberDTO memberDTO = memberService.getMemberDetail(memberId);
		//model.addAttribute("memberDTO", memberDTO);
		
		return "member/removeAccount";
	}
	
	@GetMapping("/main/about")
	public String about() {
		// 소개 페이지로 이동
		return "main/about";
	}
	/* 전시 관련 메서드 */
	
	@GetMapping("/exhibition/exhibitionList")
    public String exhibitionList(Model model) {
		ExhibitionRestController exhibitionRestController = new ExhibitionRestController();
        List<ExhibitionItem> items = exhibitionRestController.getExhibitionItems();
        model.addAttribute("exhibitionList", items);
        return "exhibition/exhibitionList";
    }
	
	@GetMapping("/exhibition/exhibitionDetail/{localId}")
	public String exhibitionDetail(Model model, @PathVariable("localId") String localId, HttpServletRequest request) {

		System.out.println(" ~~~~ exhibitionDetail : 전 localId : " + localId);

		HttpSession session = request.getSession();
		ExhibitionRestController exhibitionRestController = new ExhibitionRestController();
		ExhibitionItem items = exhibitionRestController.exhibitionDetail(localId);
		System.out.println(" ~~~~ exhibitionDetail : items imageurl : " + items.getImageUrl());
		
		model.addAttribute("exhibitionDetail", items);
		model.addAttribute("memberDTO", memberService.getMemberDetail((String)session.getAttribute("memberId")));
		
		int totalCnt = paymentService.getTotalTicketCount(localId);
		int restCnt = paymentService.getTicketRestCnt(localId);
		model.addAttribute("totalCnt", totalCnt);
		model.addAttribute("restCnt", restCnt);

		System.out.println(" ~~~~ exhibitionDetail : 후 localId : " + localId);

		return "exhibition/getExhibitionDetail";
	}
	
	@GetMapping("/exhibition/exhibitionByMuseum")
	public String exhibitionByMuseum() {
		// 미술관별 전시 페이지로 이동
		return "exhibition/exhibitionByMuseum";
	}

	@GetMapping("/exhibition/exhibitionByType")
	public String exhibitionByType() {
		// 종류별 전시 페이지로 이동
		return "exhibition/exhibitionByType";
	}

	@GetMapping("calendar/calendar")
	public String calendar() {
		// 전시 일정 페이지로 이동
		return "calendar/calendar";
	}

	@GetMapping("/exhibition/allExhibition")
	public String allExhibition() {
		// 모든 전시 페이지로 이동
		return "exhibition/allExhibition";
	}

	@GetMapping("/exhibition/presidentExhibition")
	public String presidentExhibition() {
		// 대표 전시 페이지로 이동
		return "exhibition/presidentExhibition";
	}

	/* 티켓 관련 메서드 */
	
	@GetMapping("/ticket/ticket")
	public String ticket() {
		// 티켓 구매 페이지로 이동
		return "ticket/ticket";
	}
	
	@GetMapping("/ticket/price")
	public String price() {
		// 가격 안내 페이지로 이동
		return "ticket/price";
	}
	
	/* 뉴스 관련 메서드 */
	
	@GetMapping("main/news")
	public String news() {
		// 뉴스 페이지로 이동
		return "news/news";
	}

	/* 블로그 관련 메서드 */
	
	@GetMapping("/forum/forumMain")
    public String getForumBlogList(HttpServletRequest request, Model model) {
        List<Map<String, Object>> forumList = forumService.getForumList();
        model.addAttribute("forumList", forumList); // Thymeleaf에 전달할 데이터
        return "forum/forumMain"; 
    }

	@GetMapping("/forum/registerForum")
	public String registerForum(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String memberNm = (String) session.getAttribute("memberNm");
		String maskedMemberNm = memberService.maskLastCharacter(memberNm);
		model.addAttribute("maskedMemberNm", maskedMemberNm);
		return "forum/registerForum";
	}
	
	@GetMapping("/forum/test")
	public String test(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String memberNm = (String) session.getAttribute("memberNm");
		String maskedMemberNm = memberService.maskLastCharacter(memberNm);
		model.addAttribute("maskedMemberNm", maskedMemberNm);
		return "forum/test";
	}
	
	@GetMapping("/forum/forumDetail/{forumId}")
	public String forumDetail(@PathVariable("forumId") int forumId, HttpServletRequest request, Model model) {
		
		String sessionMemberId = (String) request.getSession().getAttribute("memberId");
		boolean isLiked = forumService.hasMemberLikedPost(sessionMemberId, forumId);
		
		Map<String, Object> forum = forumService.getForumById(forumId, true);
		String memberId = (String)forum.get("memberId");
		String memberNm = memberService.getMemberNameById(memberId);
		String maskedMemberNm = memberService.maskLastCharacter(memberNm);
		
		// 댓글
		List<Map<String, Object>> commentList = commentService.getCommentList(forumId);
		
		for(Map<String, Object> comment : commentList) {
			String commentMemberId = (String)comment.get("memberId");
			String commentMemberNm = memberService.getMemberNameById(commentMemberId);
			String commentMaskedMemberNm = memberService.maskLastCharacter(commentMemberNm);
			
			comment.put("memberId", commentMaskedMemberNm);
		}
		
		model.addAttribute("forum", forum);
		model.addAttribute("memberNm", maskedMemberNm);
		model.addAttribute("isLiked", isLiked);
		model.addAttribute("commentList", commentList);
		
		return "forum/forumDetail";
	}
	
	@GetMapping("/forum/modifyForum/{forumId}")
	public String modifyForum(@PathVariable("forumId") int forumId, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String memberNm = (String) session.getAttribute("memberNm");
		String maskedMemberNm = memberService.maskLastCharacter(memberNm);
		model.addAttribute("maskedMemberNm", maskedMemberNm);
		model.addAttribute("forum", forumService.getForumById(forumId, true));
		
		return "forum/modifyForum";
	}
	
	@GetMapping("/forum/removeForum/{forumId}")
	public String removeForum(@PathVariable("forumId") int forumId, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		model.addAttribute("memberDTO", memberService.getMemberDetail((String)session.getAttribute("memberId")));
		model.addAttribute("forum", forumService.getForumById(forumId, true));
		return "forum/removeForum";
	}
	
	@GetMapping("/forum/myForumList")
	public String myForum(HttpServletRequest request, Model model) {
		// 내 게시글로 이동
		HttpSession session = request.getSession();
		MemberDTO memberDTO = memberService.getMemberDetail((String)session.getAttribute("memberId"));
		
		List<Map<String, Object>> myForumList = forumService.getMyForumList((String)session.getAttribute("memberId"));
        model.addAttribute("myForumList", myForumList); // Thymeleaf에 전달할 데이터
		
		model.addAttribute("memberDTO", memberDTO);
		if(session.getAttribute("memberId") == null) {
			return "redirect:/member/login";
		}
		return "forum/myForumList";
	}
	
	
	/* 장바구니 관련 메서드 */
	
	@GetMapping("/member/cart")
	public String cart() {
		// 장바구니로 이동
		return "member/cart";
	}
	
	
	
	
}
