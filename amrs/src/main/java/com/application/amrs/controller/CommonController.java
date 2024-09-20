package com.application.amrs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommonController {

	/* 페이지 이동용 컨트롤러 */
	
	@GetMapping("/")
	public String main() {
		// 메인 화면으로 이동
		return "main/index";
	}
	
	@GetMapping("/main/about")
	public String about() {
		// 소개 페이지로 이동
		return "main/about";
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
	
	@GetMapping("main/news")
	public String news() {
		// 뉴스 페이지로 이동
		return "news/news";
	}

	@GetMapping("/blog/blogMain")
	public String blogMain() {
		// 블로그 메인으로 이동
		return "blog/blogMain";
	}
	
	@GetMapping("/blog/myBlog")
	public String myBlog() {
		// 내 블로그로 이동
		return "blog/myBlog";
	}
	
	@GetMapping("/member/login")
	public String login() {
		// 로그인 페이지로 이동
		return "member/login";
	}

	@GetMapping("/member/mypageMain")
	public String mypageMain() {
		// 마이페이지 메인으로 이동
		return "member/mypageMain";
	}

	@GetMapping("/member/myInfo")
	public String myInfo() {
		// 개인정보확인/수정 페이지로 이동
		return "member/myInfo";
	}
	
	@GetMapping("/member/cart")
	public String cart() {
		// 장바구니로 이동
		return "member/cart";
	}
	
	
	
	
}
