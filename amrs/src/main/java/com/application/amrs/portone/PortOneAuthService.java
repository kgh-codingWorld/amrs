package com.application.amrs.portone;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PortOneAuthService {

	@Value("${iamport.api_key}")
	private String impKey;
	
	@Value("${iamport.api_secret}")
	private String impSecret;
	
	private static String accessToken = null; // 임시저장
	private static long tokenExpiration = 0; // Access Token 만료 시간(초 단위)
	
	public String getValidAccessToken() {
		if(accessToken == null || isTokenExpired()) {
			fetchNewAccessToken(); // 새 토큰 발급
		}
		return accessToken;
	}
	
	private boolean isTokenExpired() {
		return System.currentTimeMillis() / 1000 >= tokenExpiration;
	}
	
	// 새 토큰 발급
	private void fetchNewAccessToken() {
		String url = "https://api.iamport.kr/users/getToken";
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		System.out.println("IAMPORT API Key: " + impKey);
        System.out.println("IAMPORT API Secret: " + impSecret);
		
        // Map.of(): Immutable Map(불변 맵) 생성 → 내부적으로 JSON 변환 과정에서 예상치 못한 직렬화 이슈 발생
        // RestTemplate의 exchange() 메서드는 Map<String, String>을 JSON으로 변환하지만 Map.of()로 생성된 데이터가 올바르게 변환되지 않을 가능성 있음.
		// LinkedHashMap을 사용하여 JSON 변환 오류 방지
        Map<String, String> body = new LinkedHashMap<>();
		body.put("imp_key", impKey);
		body.put("imp_secret", impSecret);
		
		String jsonBody = "";
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			jsonBody = objectMapper.writeValueAsString(body);
		} catch(Exception e) {
			throw new RuntimeException("JSON 변환 실패", e);
		}
		
		System.out.println("BODY: " + jsonBody);

		// 포트원 api에 key와 secret key를 포함한 post 요청 전송
		//HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
		HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
		System.out.println("REQUEST: " + request);
		
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        System.out.println("RESPONSE: " + response);
		
		if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            System.out.println("RESPONSEBODY: " + responseBody);
            
            if (responseBody != null && (int) responseBody.get("code") == 0) {
                Map<String, Object> tokenData = (Map<String, Object>) responseBody.get("response");
                accessToken = (String) tokenData.get("access_token");
                tokenExpiration = ((Number) tokenData.get("expired_at")).longValue();
                System.out.println("새 Access Token 발급 완료: " + accessToken);
            }
        }
	}
}
