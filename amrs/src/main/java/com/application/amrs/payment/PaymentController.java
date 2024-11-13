package com.application.amrs.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;
	
	@PostMapping("/doPayment")
	public ResponseEntity<String> doPayment(@RequestBody PaymentDTO paymentDTO) {
		paymentService.registerPayment(paymentDTO);
		System.out.println("dopayment 찍히는지 확인!!!@@");
		return ResponseEntity.status(HttpStatus.OK).body("Payment data successfully saved");
	}
	
}
