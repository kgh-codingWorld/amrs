package com.application.amrs.cart;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart/api")
public class CartRestController {

	@Autowired
	private CartService cartService;
	
	@PostMapping("/registerCart")
	public ResponseEntity<Void> registerCart(@RequestBody CartDTO cartDTO) {
		if(cartDTO != null) {
			cartService.registerCart(cartDTO);
		} else {
			System.out.println("cartDTO null");
		}
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/removeCart/{cartId}")
	public ResponseEntity<?> removeCart(@PathVariable("cartId") int cartId) {
	    try {
	        boolean isDeleted = cartService.removeCart(cartId);

	        if (isDeleted) {
	            return ResponseEntity.ok().build();
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND) .body(Collections.singletonMap("message", "해당 장바구니 항목을 찾을 수 없습니다."));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "장바구니 삭제 중 오류 발생"));
	    }
	}

}
