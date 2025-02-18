package com.application.amrs.portone;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PortOneAuthService {

    private static final Logger logger = LoggerFactory.getLogger(PortOneAuthService.class);

    @Value("${iamport.api_key}")
    private String impKey;

    @Value("${iamport.api_secret}")
    private String impSecret;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    private static String accessToken = null; // 임시저장
    private static long tokenExpiration = 0;  // 만료 시간(초 단위)

    // 토큰 검증
    public String getValidAccessToken() {
        if (accessToken == null || isTokenExpired()) {
            fetchNewAccessToken();
        }
        return accessToken;
    }

    // 토큰 유효 시간
    private boolean isTokenExpired() {
        return System.currentTimeMillis() / 1000 >= tokenExpiration;
    }

    // 새 토큰 발급
    private void fetchNewAccessToken() {
        String url = "https://api.iamport.kr/users/getToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Map.of(): Immutable Map(불변 맵) 생성 → 내부적으로 JSON 변환 과정에서 예상치 못한 직렬화 이슈 발생
        // RestTemplate의 exchange() 메서드는 Map<String, String>을 JSON으로 변환하지만 Map.of()로 생성된 데이터가 올바르게 변환되지 않을 가능성 있음.
		// LinkedHashMap을 사용하여 JSON 변환 오류 방지
        Map<String, String> body = new LinkedHashMap<>();
        body.put("imp_key", impKey);
        body.put("imp_secret", impSecret);

        String jsonBody = convertToJson(body);
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        logger.info("포트원에 새 토큰 요청");

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if ((int) responseBody.get("code") == 0) {
                    Map<String, Object> tokenData = (Map<String, Object>) responseBody.get("response");
                    accessToken = (String) tokenData.get("access_token");
                    tokenExpiration = ((Number) tokenData.get("expired_at")).longValue();
                    logger.info("새 토큰 획득: {}", accessToken);
                } else {
                    logger.error("토큰 획득 실패: {}", responseBody);
                }
            } else {
                logger.error("예상치 못한 응답: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("에러", e);
        }
    }

    private String convertToJson(Map<String, String> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }
}
