package com.application.amrs.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.application.amrs.comment.CommentService;
import com.application.amrs.community.CommunityService;
import com.application.amrs.exhibition.ExhibitionService;
import com.application.amrs.exhibition.ExhibitionService.ExhibitionItem;
import com.application.amrs.member.MemberDTO;
import com.application.amrs.member.MemberService;
import com.application.amrs.payment.PaymentService;
import com.application.amrs.replyComment.ReplyCommentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommonController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private CommunityService communityService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private ReplyCommentService replyCommentService;
	
	@Autowired
	private PaymentService paymentService;
	
//	@Autowired
//    private ExhibitionRestController exhibitionRestController;
	
	/* 페이지 이동용 컨트롤러 */
	// .
	// .
	// .
	
	@GetMapping("/")
	public String main(Model model) {
		// 메인 화면으로 이동
		ExhibitionService exhibitionService = new ExhibitionService();
		List<ExhibitionItem> randomExhibitions = exhibitionService.getRandomExhibitions(3);
		
		List<Map<String, Object>> communityList = communityService.getRecentCommunityList(3);
		System.out.println("!@@@@###" + communityList);
		
		model.addAttribute("exhibitions", randomExhibitions);
		model.addAttribute("communityList", communityList);
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
	public String checkPassword() { 
		// 개인정보확인/수정 페이지 전 비밀번호 확인 페이지로 이동
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
		ExhibitionService exhibitionService = new ExhibitionService();
        List<ExhibitionItem> items = exhibitionService.getExhibitionItems();
        model.addAttribute("exhibitionList", items);
        return "exhibition/exhibitionList";
    }
	
	@GetMapping("/exhibition/exhibitionDetail/{localId}")
	public String exhibitionDetail(Model model, @PathVariable("localId") String localId, HttpServletRequest request) {

		System.out.println(" ~~~~ exhibitionDetail : 전 localId : " + localId);

		HttpSession session = request.getSession();
		ExhibitionService exhibitionService = new ExhibitionService();
		ExhibitionItem items = exhibitionService.exhibitionDetail(localId);
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

	/* 블로그 관련 메서드 */
	
	@GetMapping("/community/communityMain")
    public String getCommunityList(HttpServletRequest request, Model model) {
        List<Map<String, Object>> communityList = communityService.getCommunityList();
        model.addAttribute("communityList", communityList);
        return "community/communityMain"; 
    }

	@GetMapping("/community/registerCommunity")
	public String registerCommunity(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String memberNm = (String) session.getAttribute("memberNm");
		String maskedMemberNm = memberService.maskLastCharacter(memberNm);
		model.addAttribute("maskedMemberNm", maskedMemberNm);
		return "community/registerCommunity";
	}
	
	@GetMapping("/community/communityDetail/{communityId}")
	public String communityDetail(@PathVariable("communityId") int communityId, HttpServletRequest request, Model model) {

	    String sessionMemberId = (String) request.getSession().getAttribute("memberId");
	    
	    boolean isLiked = communityService.hasMemberLikedPost(sessionMemberId, communityId);
	    
	    Map<String, Object> community = communityService.getCommunityById(communityId, true);
	    
	    String memberId = (String) community.get("memberId");
	    String memberNm = memberService.getMemberNameById(memberId);
	    
	    String maskedMemberNm = memberService.maskLastCharacter(memberNm);

	    List<Map<String, Object>> commentList = commentService.getCommentList(communityId);
	    
	    for (Map<String, Object> comment : commentList) {
	        String commentMemberId = (String) comment.get("memberId");
	        String commentMemberNm = memberService.getMemberNameById(commentMemberId);
	        
	        String commentMaskedMemberNm = memberService.maskLastCharacter(commentMemberNm);
	        
	        comment.put("memberNm", commentMaskedMemberNm);
	        comment.put("memberId", commentMemberId);

	        int commentId = (int) comment.get("commentId");

	        List<Map<String, Object>> replyCommentList = replyCommentService.getReplyCommentList(commentId);

	        for (Map<String, Object> replyComment : replyCommentList) {
	            String replyMemberId = (String) replyComment.get("memberId");
	            String replyMemberNm = memberService.getMemberNameById(replyMemberId);

	            String replyMaskedMemberNm = memberService.maskLastCharacter(replyMemberNm);

	            replyComment.put("memberNm", replyMaskedMemberNm);
	            replyComment.put("memberId", replyMemberId);
	        }

	        comment.put("replyCommentList", replyCommentList);
	    }

	    int commentCnt = commentService.countCommentByCommunityId(communityId);
	    
	    model.addAttribute("commentCnt", commentCnt); 	// 댓글 수
	    model.addAttribute("community", community);     // 게시글 정보
	    model.addAttribute("memberNm", maskedMemberNm); // 게시글 작성자의 마스킹된 이름
	    model.addAttribute("isLiked", isLiked);      	// 현재 사용자의 '좋아요' 여부
	    model.addAttribute("commentList", commentList); // 댓글 목록 (대댓글 포함)

	    return "community/communityDetail";
	}

	
	@GetMapping("/community/modifyCommunity/{communityId}")
	public String modifyCommunity(@PathVariable("communityId") int communityId, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String memberNm = (String) session.getAttribute("memberNm");
		String maskedMemberNm = memberService.maskLastCharacter(memberNm);
		model.addAttribute("maskedMemberNm", maskedMemberNm);
		model.addAttribute("community", communityService.getCommunityById(communityId, true));
		
		return "community/modifyCommunity";
	}
	
	@GetMapping("/community/removeCommunity/{communityId}")
	public String removeCommunity(@PathVariable("communityId") int communityId, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		model.addAttribute("memberDTO", memberService.getMemberDetail((String)session.getAttribute("memberId")));
		model.addAttribute("community", communityService.getCommunityById(communityId, true));
		return "community/removeCommunity";
	}
	
	@GetMapping("/community/myCommunityPostList")
	public String myCommunityPostList(HttpServletRequest request, Model model) {
		// 내 게시글로 이동
		HttpSession session = request.getSession();
		MemberDTO memberDTO = memberService.getMemberDetail((String)session.getAttribute("memberId"));
		
		List<Map<String, Object>> myCommunityList = communityService.getMyCommunityList((String)session.getAttribute("memberId"));
        model.addAttribute("myCommunityList", myCommunityList);
		
		model.addAttribute("memberDTO", memberDTO);
		if(session.getAttribute("memberId") == null) {
			return "redirect:/member/login";
		}
		return "community/myCommunityPostList";
	}
	
	@GetMapping("/member/cartMain")
	public String cartMain() {
		return "member/cartMain";
	}
	
}
