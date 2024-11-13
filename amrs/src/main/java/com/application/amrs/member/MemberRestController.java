package com.application.amrs.member;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/certifications")
//@RequestMapping("/member")
public class MemberRestController {

	@Autowired
	private MemberService memberService;
	
	@PostMapping("/confirm")
	public ResponseEntity<String> confirmCertification(@RequestBody Map<String, Object> payload){
		
		String impUid = (String) payload.get("imp_uid");
		
		if(impUid == null) {
			return ResponseEntity.badRequest().body("impUid가 누락되었습니다.");
		}

//		String accessToken = memberService.getPortOneAccessToken(); // 발급받은 Access Token 사용
		String accessToken = "d1b7c54fcfaaddcc340506489a45607f8a8eb365";
		System.out.println("accessToken~!!!!" + accessToken);

		// imp_uid로 본인인증 정보 확인
        String certificationUrl = "https://api.iamport.kr/certifications/" + impUid;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(certificationUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> responseBody = response.getBody();
        
        if (responseBody != null && (boolean) responseBody.get("success")) {
            return ResponseEntity.ok("본인인증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인인증 실패");
        }
	}
	
	// 포트원 인증 관련 설정
//	@Value("${iamport.api_key}")
//	private String iamportApiKey;
//	
//	@Value("${iamport.api_secret}")
//    private String iamportApiSecret;
//	
//	@PostMapping("/phoneAuthentication")
//	public ResponseEntity<Map<String, Object>> phoneAuthentication(@RequestBody Map<String, String> requestData) {
//	    String impUid = requestData.get("imp_uid");
//
//	    // 1. Access Token 요청 (임포트 서버)
//	    RestTemplate restTemplate = new RestTemplate();
//	    String tokenUrl = "https://api.iamport.kr/users/getToken";
//	    Map<String, String> tokenRequest = new HashMap<>();
//	    tokenRequest.put("imp_key", iamportApiKey);
//	    tokenRequest.put("imp_secret", iamportApiSecret);
//
//	    // Access Token 요청
//	    ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);
//	    String accessToken = (String) ((Map<String, Object>) tokenResponse.getBody().get("response")).get("access_token");
//
//	    // 2. 본인인증 결과 요청
//	    String certificationUrl = "https://api.iamport.kr/certifications/" + impUid;
//
//	    // 헤더 설정
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.set("Authorization", accessToken);
//	    HttpEntity<String> entity = new HttpEntity<>(headers);
//
//	    // GET 요청 (imp_uid로 인증 정보 요청)
//	    ResponseEntity<Map> certificationResponse = restTemplate.exchange(certificationUrl, HttpMethod.GET, entity, Map.class);
//
//	    Map<String, Object> response = certificationResponse.getBody();
//	    if (response != null && (boolean) response.get("success")) {
//	        // 인증 성공 처리
//	        Map<String, Object> successResponse = new HashMap<>();
//	        successResponse.put("message", "인증 성공");
//	        return ResponseEntity.ok(successResponse);
//	    } else {
//	        // 인증 실패 처리
//	        Map<String, Object> errorResponse = new HashMap<>();
//	        errorResponse.put("message", "인증 실패");
//	        return ResponseEntity.badRequest().body(errorResponse);
//	    }
//	}
}
