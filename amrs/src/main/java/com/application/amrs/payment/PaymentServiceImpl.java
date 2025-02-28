package com.application.amrs.payment;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService{

	@Autowired
	private PaymentDAO paymentDAO;
	
	@Override
	public int registerPayment(PaymentDTO paymentDTO) {
		paymentDAO.insertPayment(paymentDTO);
		return paymentDTO.getPaymentId();
	}


	@Override
	public int getTotalTicketCount(String localId) {
		Integer totalCount = paymentDAO.selectTotalTicketCnt(localId);
		if(totalCount == null || totalCount == 0) {
			totalCount = 20;
		} else {
			totalCount = paymentDAO.selectRestTicketCnt(localId);
		}
		int totalCnt = totalCount;
		return totalCnt;
	}
	
	@Override
	public int getTicketRestCnt(String localId) {
		Integer restCount = paymentDAO.selectRestTicketCnt(localId);
		if (restCount == null) {
			restCount = 20;
	    } 
		int restCnt = restCount;
		return restCnt;
	}

	@Override
	public List<Map<String, Object>> getPaymentList(String memberId) {
		return paymentDAO.selectPaymentList(memberId);
	}


	@Override
	public PaymentDTO getPaymentDetail(int paymentId) {
		return paymentDAO.selectPaymentDetail(paymentId);
	}

}
