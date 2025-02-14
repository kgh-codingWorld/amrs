package com.application.amrs.member;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.application.amrs.portone.PortOneAuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/member/api")
public class MemberRestController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private PortOneAuthService portOneAuthService;
	
	// 아이디 중복체크
	@PostMapping("/checkDuplicatedId")
    public ResponseEntity<String> checkDuplicatedId(@RequestParam("memberId") String memberId) {
        String isDuplicated = memberService.checkDuplicatedId(memberId);
        return ResponseEntity.ok(isDuplicated);
    }
	
	// 비밀번호 일치 여부
	@PostMapping("/isValidPasswd")
    public ResponseEntity<Boolean> isValidPasswd(@RequestParam("passwd") String passwd, 
                                                 @RequestParam("memberId") String memberId) {
        boolean isValid = memberService.isValidPasswd(passwd, memberId);
        return ResponseEntity.ok(isValid);  // `true` 또는 `false` 반환
    }
	
	// 비밀번호 수정
	@PostMapping("/modifyMyPasswd")
	public ResponseEntity<String> modifyMyPasswd(@RequestParam("newPasswd") String newPasswd, 
												 @RequestParam("memberId") String memberId,
												 HttpServletRequest request, HttpServletResponse response) {
		String result = memberService.modifyMyPasswd(newPasswd, memberId);
		
		// 세션 무효화
	    request.getSession().invalidate();
	    
	    // 쿠키 삭제 (HttpServletResponse 사용)
	    Cookie cookie = new Cookie("JSESSIONID", null);
	    cookie.setMaxAge(0);
	    cookie.setPath("/");
	    response.addCookie(cookie);
	    
	    Cookie authToken = new Cookie("authToken", null);
	    authToken.setMaxAge(0);
	    authToken.setPath("/");
	    response.addCookie(authToken);
		return ResponseEntity.ok(result);
	}
	
	// 휴대폰 번호 수정
	@PostMapping("/newMemberHp")
	public ResponseEntity<String> newMemberHp(@RequestParam("newMemberHp") String newMemberHp, @RequestParam("memberId")String memberId) {
		String result = memberService.modifyMemberHp(newMemberHp, memberId);
		return ResponseEntity.ok(result);
	}
	
	// 본인인증 정보 조회
		@PostMapping("/certifications/confirm")
		public ResponseEntity<String> confirmCertification(@RequestBody Map<String, Object> payload){
			String impUid = (String) payload.get("imp_uid");
			
			if(impUid == null) {
				return ResponseEntity.badRequest().body("impUid가 누락되었습니다.");
			}

			// 최신 토큰
			String accessToken = portOneAuthService.getValidAccessToken();

			// imp_uid로 본인인증 정보 확인
	        String certificationUrl = "https://api.iamport.kr/certifications/" + impUid;

	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "Bearer " + accessToken);
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        // 포트원 api에 토큰을 포함한 get 요청을 보내기 위함(반드시 Authorization과 헤더(토큰)가 필요)
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        
	        ResponseEntity<Map> response = restTemplate.exchange(certificationUrl, HttpMethod.GET, entity, Map.class);

	        Map<String, Object> responseBody = response.getBody();

	        if (responseBody != null) {
	            Integer responseCode = (Integer) responseBody.get("code");  // 성공 여부 확인
	            Map<String, Object> responseData = (Map<String, Object>) responseBody.get("response");  // 응답 데이터
	            
	            if (responseCode != null && responseCode == 0) {  // 성공 여부 확인
	                Boolean isCertified = responseData != null ? (Boolean) responseData.get("certified") : false;
	                
	                if (Boolean.TRUE.equals(isCertified)) {  // 인증 여부 확인
	                    System.out.println("본인인증 성공");
	                    return ResponseEntity.ok("본인인증 성공");
	                } else {
	                    System.out.println("본인인증 실패: " + responseBody);
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인인증 실패");
	                }
	            } else {
	                System.out.println("본인인증 실패: " + responseBody);
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인인증 실패: " + responseBody);
	            }
	        } else {
	            System.out.println("본인인증 응답이 null입니다.");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("IAMPORT API 응답이 null입니다.");
	        }

		}
}
