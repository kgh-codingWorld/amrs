package com.application.amrs.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
		return ResponseEntity.ok(null);
	}
}
