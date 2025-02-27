package com.application.amrs.payment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentDAO {

	public void insertPayment(PaymentDTO paymentDTO);
	public Integer selectTotalTicketCnt(String localId);
	public Integer selectRestTicketCnt(String paymentId);
	public List<Map<String, Object>> selectPaymentList(String memberId);
	public PaymentDTO selectPaymentDetail(int paymentId);
	
}
