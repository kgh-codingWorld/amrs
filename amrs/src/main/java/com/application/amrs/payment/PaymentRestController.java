package com.application.amrs.payment;

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
	public ResponseEntity<String> doPayment(@RequestBody PaymentDTO paymentDTO) {
		int restCnt = paymentService.getTicketRestCnt(paymentDTO.getLocalId());
		
		if(restCnt<=0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잔여 티켓이 없습니다.");
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("doPayment");
		// paymentId 반환시켜서 결제 정보 저장 시 함께 저장할 거임
		int paymentId = paymentService.registerPayment(paymentDTO);
		stopWatch.stop();
		System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + " ms");
		return ResponseEntity.status(HttpStatus.OK).body("payment successfully" + paymentId);
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
