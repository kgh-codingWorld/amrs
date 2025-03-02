package com.application.amrs.payment;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {
	
	private int paymentId;
	private String localId;
	private String memberId;
	private int price;
	private int totalCnt;
	private int orderCnt;
	private int restCnt;
	private String exhibitionTitle;
	private String exhibitionImage;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date reservDate;
	private Date createDt;
	
	private boolean hasReview;
}
