package com.application.amrs.payment;

import java.util.List;
import java.util.Map;

public interface PaymentService {

	public void registerPayment(PaymentDTO paymentDTO);
	public int getTotalTicketCount(String localId);
	public int getTicketRestCnt(String localId);
	public List<Map<String, Object>> getPaymentList(String memberId);
	public PaymentDTO getPaymentInfo(String memberId);
}
