package com.application.amrs.payment;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	
	@PostMapping("/verifyPayment")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> payload) {
        String impUid = (String) payload.get("imp_uid");

        if (impUid == null) {
            return ResponseEntity.badRequest().body("impUid가 누락되었습니다.");
        }

        // 최신 토큰 가져오기
        String accessToken = portOneAuthService.getValidAccessToken();

        // imp_uid로 결제 정보 확인
        String paymentUrl = "https://api.iamport.kr/payments/" + impUid;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(paymentUrl, HttpMethod.GET, entity, Map.class);

        Map<String, Object> responseBody = response.getBody();

        if (responseBody != null) {
            Integer responseCode = (Integer) responseBody.get("code");
            Map<String, Object> responseData = (Map<String, Object>) responseBody.get("response");

            if (responseCode != null && responseCode == 0) {
                // 결제 검증 수행
                Integer paidAmount = responseData != null ? (Integer) responseData.get("amount") : null;
                String status = responseData != null ? (String) responseData.get("status") : "failed";

                // 결제 금액 및 상태 확인
                if ("paid".equals(status) && paidAmount != null) {
                    System.out.println("결제 검증 성공");
                    return ResponseEntity.ok("결제 검증 성공: " + paidAmount + "원 결제됨");
                } else {
                    System.out.println("결제 검증 실패: " + responseBody);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 검증 실패");
                }
            } else {
                System.out.println("결제 검증 실패: " + responseBody);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 검증 실패: " + responseBody);
            }
        } else {
            System.out.println("결제 검증 응답이 null입니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("IAMPORT API 응답이 null입니다.");
        }
    }
	
	@PostMapping("/doPayment")
	public ResponseEntity<String> doPayment(@RequestBody PaymentDTO paymentDTO) {
		paymentService.registerPayment(paymentDTO);
		return ResponseEntity.status(HttpStatus.OK).body("payment successfully");
	}
}
