package com.application.amrs.portone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.amrs.member.MemberService;

@RestController
public class PortOneRestController {

	@Autowired
	private MemberService memberService;
	
	@GetMapping("/portone/accessToken")
	public ResponseEntity<String> getPortOneAccessToken() {
		String accessToken = memberService.getPortOneAccessToken();
		return ResponseEntity.ok(accessToken);
	}
}
