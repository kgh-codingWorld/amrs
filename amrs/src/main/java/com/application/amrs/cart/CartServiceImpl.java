package com.application.amrs.cart;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private CartDAO cartDAO;
	
	@Override
	public List<Map<String, Object>> getCartList() {
		return cartDAO.selectCartList();
	}

	@Override
	public void registerCart(CartDTO cartDTO) {
		cartDAO.insertCart(cartDTO);
	}

	@Override
	public boolean removeCart(int cartId) {
		int deletedRows = cartDAO.deleteCart(cartId);
	    return deletedRows > 0;
	}

}
