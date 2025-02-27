package com.application.amrs.payment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.application.amrs.portone.PortOneAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/payment/api")
public class PaymentRestController {

	@Autowired
	private PaymentService paymentService;
	
	@Autowired
    private PortOneAuthService portOneAuthService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@PostMapping("/verifyPayment")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> payload) {
        String impUid = (String) payload.get("imp_uid");
        if (impUid == null) {
            return ResponseEntity.badRequest().body("impUid가 누락되었습니다.");
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("checking accessToken");
        String accessToken = portOneAuthService.getValidAccessToken();
        stopWatch.stop();
        System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + " ms");
        String paymentUrl = "https://api.iamport.kr/payments/" + impUid;
        HttpHeaders headers = createAuthHeaders(accessToken);
//        headers.set("Authorization", "Bearer " + accessToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(paymentUrl, HttpMethod.GET, entity, Map.class);
        return processPaymentVerificationResponse(response);
    }
	
	@PostMapping("/doPayment")
	public ResponseEntity<?> doPayment(@RequestBody Object request) {
		try {
			List<PaymentDTO> payments = new ArrayList<>();
			
			if (request instanceof List) {
	            // JSON 배열인 경우
	            payments = (List<PaymentDTO>) request;
	        } else if (request instanceof Map) {
	            // JSON 객체(단일 결제)인 경우
	            PaymentDTO singlePayment = new ObjectMapper().convertValue(request, PaymentDTO.class);
	            payments.add(singlePayment);
	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Collections.singletonMap("message", "잘못된 요청 형식입니다."));
	        }
			
			 List<Map<String, Object>> responses = new ArrayList<>();
			 
			 StopWatch stopWatch = new StopWatch();
	        stopWatch.start("doPayment");

	        for (PaymentDTO paymentDTO : payments) {
	            int restCnt = paymentService.getTicketRestCnt(paymentDTO.getLocalId());

	            if (restCnt <= 0) {
	                responses.add(Map.of(
	                    "message", "잔여 티켓이 없습니다.",
	                    "localId", paymentDTO.getLocalId(),
	                    "status", "failed"
	                ));
	                continue; // 다음 결제 요청 처리
	            }

	            if (paymentDTO.getOrderCnt() > restCnt) {
	                responses.add(Map.of(
	                    "message", "잔여 티켓보다 많은 수량을 예매할 수 없습니다.",
	                    "localId", paymentDTO.getLocalId(),
	                    "status", "failed"
	                ));
	                continue;
	            }

	            int paymentId = paymentService.registerPayment(paymentDTO);
	            System.out.println(paymentId);

	            responses.add(Map.of(
	                "message", "결제 성공",
	                "paymentId", paymentId,
	                "localId", paymentDTO.getLocalId(),
	                "status", "success"
	            ));
	        }

	        stopWatch.stop();
	        System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + " ms");

	        return ResponseEntity.ok(responses);
			
		} catch(Exception e) {
			e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "결제 처리 중 오류 발생"));
		}
	}
	
	private HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private ResponseEntity<String> processPaymentVerificationResponse(ResponseEntity<Map> response) {
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("API 응답이 null입니다.");
        }

        Map<String, Object> responseBody = response.getBody();
        Integer responseCode = (Integer) responseBody.get("code");
        Map<String, Object> responseData = (Map<String, Object>) responseBody.get("response");

        if (responseCode != null && responseCode == 0 && responseData != null) {
            Integer paidAmount = (Integer) responseData.get("amount");
            String status = (String) responseData.getOrDefault("status", "failed");

            if ("paid".equals(status) && paidAmount != null) {
                return ResponseEntity.ok("결제 검증 성공: " + paidAmount + "원 결제됨");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 검증 실패: " + responseBody);
    }
}
