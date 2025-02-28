package com.application.amrs.cart;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartDAO {

	public List<Map<String, Object>> selectCartList();
	public void insertCart(CartDTO cartDTO);
	public int deleteCart(int cartId);
}
