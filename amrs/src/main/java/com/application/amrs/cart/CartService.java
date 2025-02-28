package com.application.amrs.cart;

import java.util.List;
import java.util.Map;

public interface CartService {
	
	public List<Map<String, Object>> getCartList();
	public void registerCart(CartDTO cartDTO);
	public boolean removeCart(int cartId);
}
