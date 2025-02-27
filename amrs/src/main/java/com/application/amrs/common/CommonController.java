package com.application.amrs.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.application.amrs.cart.CartService;
import com.application.amrs.comment.CommentService;
import com.application.amrs.community.CommunityService;
import com.application.amrs.exhibition.ExhibitionService;
import com.application.amrs.exhibition.ExhibitionService.ExhibitionItem;
import com.application.amrs.member.MemberDTO;
import com.application.amrs.member.MemberService;
import com.application.amrs.payment.PaymentDTO;
import com.application.amrs.payment.PaymentService;
import com.application.amrs.replyComment.ReplyCommentService;
import com.application.amrs.review.ReviewDTO;
import com.application.amrs.review.ReviewService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommonController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ExhibitionService exhibitionService;
	
	@Autowired
	private CommunityService communityService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private ReplyCommentService replyCommentService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private CartService cartService;
	/* 페이지 이동용 컨트롤러 */
	// .
	// .
	// .
	
	// 메인 화면으로 이동
	@GetMapping("/")
	public String main(Model model) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("load main");
		
		model.addAttribute("exhibitionList", exhibitionService.getRandomExhibitions(3));
		model.addAttribute("communityList", communityService.getCommunityList(3));
		stopWatch.stop();
		System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + " ms");
		
		return "main/index";
	}
	
	/*회원 관련 메서드*/
	
	// 로그인 페이지로 이동
	@GetMapping("/member/login")
	public String login() {
		return "member/login";
	}
	
	// 로그아웃된 상태로 메인 화면 이동
	@GetMapping("/member/logout")
	public String logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/";
	}
	
	@GetMapping("/member/findId")
	public String findAccount() {
		return "member/findId";
	}
	
	@GetMapping("/member/resetPassword")
	public String resetPassword() {
		return "member/resetPassword";
	}
	
	
	// 회원가입 페이지로 이동
	@GetMapping("/member/registerMember")
	public String registerMember() {
		return "member/registerMember";
	}

	// 마이페이지 메인으로 이동
	@GetMapping("/member/mypageMain")
	public String mypageMain(HttpServletRequest request, Model model) {
		String memberId = getSessionMemberId(request);
		if(memberId == null) return "redirect:/member/login";
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("load mypageMain");
		
		// 현재 로그인된 아이디로 payment 정보 호출
		List<Map<String, Object>> paymentList = paymentService.getPaymentList(memberId);
		for(Map<String, Object> payment : paymentList) {
			int paymentId = (int) payment.get("paymentId");
			Integer reviewId = reviewService.getReviewIdByPaymentId(paymentId);
			payment.put("reviewId", reviewId);
			payment.put("hasReview", reviewId != null);
		}
		
		model.addAttribute("paymentList", paymentList);
		
		stopWatch.stop();
		System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + " ms");
		
		return "member/mypageMain";
	}
	
	// 개인정보확인/수정 페이지 전 비밀번호 확인 페이지로 이동
	@GetMapping("/member/checkPassword")
	public String checkPassword() { 
		return "member/checkPassword";
	}
	
	// 개인정보확인/수정 페이지로 이동
	@GetMapping("/member/myInfo")
	public String myInfo(HttpServletRequest request, Model model) {
		String memberId = getSessionMemberId(request);
		if(memberId == null) return "redirect:/member/login";
		
		model.addAttribute("memberDTO", memberService.getMemberDetail(memberId));
		return "member/myInfo";
	}
	
	// 회원탈퇴 페이지로 이동
	@GetMapping("/member/removeAccount")
	public String removeAccount(HttpServletRequest request, Model model) {
		String memberId = getSessionMemberId(request);
		model.addAttribute("memberDTO", memberService.getMemberDetail(memberId));
		return "member/removeAccount";
	}
	
	// 소개 페이지로 이동
	@GetMapping("/main/about")
	public String about() {
		return "main/about";
	}
	
	/* 전시 관련 메서드 */

	// 모든 전시 페이지로 이동
	@GetMapping("/exhibition/exhibitionList")
	public String exhibitionList(Model model) {
	    StopWatch stopWatch = new StopWatch();
	    stopWatch.start("load exhibitionList");

	    List<ExhibitionItem> exhibitionList = exhibitionService.getExhibitionItems();

	    model.addAttribute("exhibitionList", exhibitionList);

	    stopWatch.stop();
	    System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + " ms");

	    return "exhibition/exhibitionList";
	}
	
	// 현재 전시 페이지로 이동
	@GetMapping("/exhibition/exhibitionNow")
	public String exhibitionNow(Model model) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("load exhibitionNow");
		
		List<ExhibitionItem> items = exhibitionService.getShowingExhibitionItems();
		model.addAttribute("showingItems", items);
    	stopWatch.stop();
        System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + " ms");
        
    	return "exhibition/exhibitionNow";
	}
	
	// 과거 전시 페이지로 이동
	@GetMapping("/exhibition/exhibitionArchive")
	public String exhibitionArchive(Model model) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("load exhibitionArchive");
		
		List<ExhibitionItem> items = exhibitionService.getShowedExhibitionItems();
		
		stopWatch.stop();
        System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + " ms");
        
		model.addAttribute("showedItems", items);
		return "exhibition/exhibitionArchive";
	}
	
	// 전시 상세보기 페이지로 이동
	@GetMapping("/exhibition/exhibitionDetail/{localId}")
	public String exhibitionDetail(@PathVariable("localId") String localId, HttpServletRequest request, Model model) {
		String memberId = getSessionMemberId(request);
		if(memberId == null) return "redirect:/member/login";

		StopWatch stopWatch = new StopWatch();
		stopWatch.start("load exhibitionDetail");
		
		ExhibitionItem exhibitionItem = exhibitionService.exhibitionDetail(localId);
			
		stopWatch.stop();
        System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + " ms");
        
        addExhibitionAttributes(model, memberId, localId, exhibitionItem);
		
		return "exhibition/getExhibitionDetail";
	}
	
	// 전시 속성 모델에 추가
	private void addExhibitionAttributes(Model model, String memberId, String localId, ExhibitionItem exhibitionItem) {
		model.addAttribute("exhibitionDetail", exhibitionItem);
		model.addAttribute("isExpired", exhibitionItem.getIsExpired());
		model.addAttribute("memberDTO", memberService.getMemberDetail(memberId));
		model.addAttribute("totalCnt", paymentService.getTotalTicketCount(localId));
		model.addAttribute("restCnt", paymentService.getTicketRestCnt(localId));
	}
	
	
	/* 리뷰 관련 메서드 */
	
	@GetMapping("/review/registerReview/{paymentId}")
	public String registerReview(@PathVariable("paymentId") int paymentId, HttpServletRequest request, Model model) {
		String memberId = getSessionMemberId(request);
		if(memberId == null) return "redirect:/member/login";
		PaymentDTO payment = paymentService.getPaymentDetail(paymentId);
		model.addAttribute("payment", payment);
		return "review/registerReview";
	}
	
	@GetMapping("/review/reviewDetail/{paymentId}/{reviewId}")
	public String reviewDetail(@PathVariable("paymentId") int paymentId, @PathVariable("reviewId") int reviewId, HttpServletRequest request, Model model) {
		String memberId = getSessionMemberId(request);
		if(memberId == null) return "redirect:/member/login";
		System.out.println("reviewIdData before getReviewDetail: " + reviewId);
		ReviewDTO review = reviewService.getReviewDetail(paymentId, reviewId);
		System.out.println("reviewData after getReviewDetail: " + review);
		PaymentDTO payment = paymentService.getPaymentDetail(paymentId);
		model.addAttribute("payment", payment);
		model.addAttribute("review", review);
		return "review/reviewDetail";
	}

	/* 커뮤니티 관련 메서드 */
	
	// 커뮤니티 메인 페이지로 이동
	@GetMapping("/community/communityMain")
    public String getCommunityList(Model model) {
        model.addAttribute("communityList", communityService.getCommunityList(null));
        return "community/communityMain"; 
    }

	// 커뮤니티 작성 페이지로 이동
	@GetMapping("/community/registerCommunity")
	public String registerCommunity(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String memberNm = (String) session.getAttribute("memberNm");
		String maskedMemberNm = memberService.maskLastCharacter(memberNm);
		model.addAttribute("maskedMemberNm", maskedMemberNm);
		return "community/registerCommunity";
	}
	
	// 커뮤니티 상세보기 페이지로 이동
	@GetMapping("/community/communityDetail/{communityId}")
	public String communityDetail(@PathVariable("communityId") int communityId, HttpServletRequest request, Model model) {
	    String sessionMemberId = getSessionMemberId(request);
	    if(sessionMemberId == null) return "redirect:/member/login";
	    
	    Map<String, Object> community = communityService.getCommunityById(communityId, true);
	    
	    addCommunityAttributes(model, sessionMemberId, communityId, community);
	    return "community/communityDetail";
	}
	
	// 커뮤니티 속성 모델에 추가
	private void addCommunityAttributes(Model model, String sessionMemberId, int communityId, Map<String, Object> community) {
        boolean isLiked = communityService.hasMemberLikedPost(sessionMemberId, communityId);
        String memberId = (String) community.get("memberId");
        String maskedMemberNm = memberService.maskLastCharacter(memberService.getMemberNameById(memberId));

        List<Map<String, Object>> commentList = commentService.getCommentList(communityId);
        commentList.forEach(comment -> maskCommentData(comment));

        model.addAttribute("commentCnt", commentService.countCommentByCommunityId(communityId));
        model.addAttribute("community", community);
        model.addAttribute("memberNm", maskedMemberNm);
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("commentList", commentList);
    }

	// 이름 아스트리크 처리
	private void maskCommentData(Map<String, Object> comment) {
        String commentMemberId = (String) comment.get("memberId");
        comment.put("memberNm", memberService.maskLastCharacter(memberService.getMemberNameById(commentMemberId)));

        List<Map<String, Object>> replyCommentList = replyCommentService.getReplyCommentList((int) comment.get("commentId"));
        replyCommentList.forEach(replyComment -> {
            String replyMemberId = (String) replyComment.get("memberId");
            replyComment.put("memberNm", memberService.maskLastCharacter(memberService.getMemberNameById(replyMemberId)));
        });

        comment.put("replyCommentList", replyCommentList);
    }
	
	// 커뮤니티 수정 페이지로 이동
	@GetMapping("/community/modifyCommunity/{communityId}")
	public String modifyCommunity(@PathVariable("communityId") int communityId, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String memberNm = (String) session.getAttribute("memberNm");
		String maskedMemberNm = memberService.maskLastCharacter(memberNm);
		model.addAttribute("maskedMemberNm", maskedMemberNm);
		model.addAttribute("community", communityService.getCommunityById(communityId, true));
		
		return "community/modifyCommunity";
	}
	
	// 커뮤니티 삭제 페이지로 이동
	@GetMapping("/community/removeCommunity/{communityId}")
	public String removeCommunity(@PathVariable("communityId") int communityId, HttpServletRequest request, Model model) {
		if(getSessionMemberId(request) == null) return "redirect:/member/login";
		model.addAttribute("memberDTO", memberService.getMemberDetail(getSessionMemberId(request)));
		model.addAttribute("community", communityService.getCommunityById(communityId, true));
		return "community/removeCommunity";
	}
	
	// 내가 쓴 글 페이지로 이동
	@GetMapping("/community/myCommunityPostList")
	public String myCommunityPostList(HttpServletRequest request, Model model) {
		String memberId = getSessionMemberId(request);
		if(memberId == null) return "redirect:/member/login";
		
		MemberDTO memberDTO = memberService.getMemberDetail(memberId);
        model.addAttribute("myCommunityList", communityService.getMyCommunityList(memberId));
		model.addAttribute("memberDTO", memberDTO);
		return "community/myCommunityPostList";
	}
	
	/* 장바구니 관련 메서드 */
	@GetMapping("/member/cartMain")
	public String cartMain(HttpServletRequest request, Model model) {
		String memberId = getSessionMemberId(request);
		if(memberId == null) return "redirect:/member/login";
		
		List<Map<String, Object>> carts = cartService.getCartList();
		
		Map<String, Integer> restCntMap = new HashMap<>();
	    Map<String, Integer> totalCntMap = new HashMap<>();
		
		for(Map<String, Object> cart : carts) {
			String localId = cart.get("localId").toString();
			// 중복 조회 방지
	        if (!restCntMap.containsKey(localId)) {
	            restCntMap.put(localId, paymentService.getTicketRestCnt(localId));
	            totalCntMap.put(localId, paymentService.getTotalTicketCount(localId));
	        }
		}
		
		model.addAttribute("cartList", carts);
	    model.addAttribute("restCntMap", restCntMap);  // localId별 잔여 티켓 수
	    model.addAttribute("totalCntMap", totalCntMap); // localId별 전체 티켓 수
	    model.addAttribute("memberNm", memberService.getMemberNameById(memberId));
		return "member/cartMain";
	}
	
	/* 공통 메서드 */
    private String getSessionMemberId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (String) session.getAttribute("memberId");
    }
	
}
