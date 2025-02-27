package com.application.amrs.cart;

import java.util.Date;

import lombok.Data;

@Data
public class CartDTO {

	private int cartId;
	private String localId;
	private String memberId;
	private int orderCnt;
	private String exhibitionTitle;
	private String exhibitionImage;
	private Date reservDate;
	private Date createDt;
	private Date updateDt;
}
