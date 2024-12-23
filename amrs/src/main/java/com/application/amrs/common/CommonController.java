package com.application.amrs.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.application.amrs.blog.BlogService;
import com.application.amrs.comment.CommentService;
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
	private BlogService blogService;
	
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
	
	@GetMapping("/blog/blogMain")
    public String getBlogList(HttpServletRequest request, Model model) {
        List<Map<String, Object>> blogList = blogService.getBlogList();
        model.addAttribute("blogList", blogList);
        return "blog/blogMain"; 
    }

	@GetMapping("/blog/registerBlog")
	public String registerBlog(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String memberNm = (String) session.getAttribute("memberNm");
		String maskedMemberNm = memberService.maskLastCharacter(memberNm);
		model.addAttribute("maskedMemberNm", maskedMemberNm);
		return "blog/registerBlog";
	}
	
	@GetMapping("/blog/blogDetail/{blogId}")
	public String blogDetail(@PathVariable("blogId") int blogId, HttpServletRequest request, Model model) {

	    String sessionMemberId = (String) request.getSession().getAttribute("memberId");
	    
	    boolean isLiked = blogService.hasMemberLikedPost(sessionMemberId, blogId);
	    
	    Map<String, Object> blog = blogService.getBlogById(blogId, true);
	    
	    String memberId = (String) blog.get("memberId");
	    String memberNm = memberService.getMemberNameById(memberId);
	    
	    String maskedMemberNm = memberService.maskLastCharacter(memberNm);

	    List<Map<String, Object>> commentList = commentService.getCommentList(blogId);
	    
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

	    int commentCnt = commentService.countCommentByBlogId(blogId);
	    
	    model.addAttribute("commentCnt", commentCnt); 	// 댓글 수
	    model.addAttribute("blog", blog);          	// 게시글 정보
	    model.addAttribute("memberNm", maskedMemberNm); // 게시글 작성자의 마스킹된 이름
	    model.addAttribute("isLiked", isLiked);      	// 현재 사용자의 '좋아요' 여부
	    model.addAttribute("commentList", commentList); // 댓글 목록 (대댓글 포함)

	    return "blog/blogDetail";
	}

	
	@GetMapping("/blog/modifyBlog/{blogId}")
	public String modifyBlog(@PathVariable("blogId") int blogId, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String memberNm = (String) session.getAttribute("memberNm");
		String maskedMemberNm = memberService.maskLastCharacter(memberNm);
		model.addAttribute("maskedMemberNm", maskedMemberNm);
		model.addAttribute("blog", blogService.getBlogById(blogId, true));
		
		return "blog/modifyBlog";
	}
	
	@GetMapping("/blog/removeBlog/{blogId}")
	public String removeBlog(@PathVariable("blogId") int blogId, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		model.addAttribute("memberDTO", memberService.getMemberDetail((String)session.getAttribute("memberId")));
		model.addAttribute("blog", blogService.getBlogById(blogId, true));
		return "blog/removeBlog";
	}
	
	@GetMapping("/blog/myBlogPostList")
	public String myBlogPostList(HttpServletRequest request, Model model) {
		// 내 게시글로 이동
		HttpSession session = request.getSession();
		MemberDTO memberDTO = memberService.getMemberDetail((String)session.getAttribute("memberId"));
		
		List<Map<String, Object>> myBlogList = blogService.getMyBlogList((String)session.getAttribute("memberId"));
        model.addAttribute("myBlogList", myBlogList);
		
		model.addAttribute("memberDTO", memberDTO);
		if(session.getAttribute("memberId") == null) {
			return "redirect:/member/login";
		}
		return "blog/myBlogPostList";
	}
	
	@GetMapping("/member/cartMain")
	public String cartMain() {
		return "member/cartMain";
	}
	
}
