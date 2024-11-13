package com.application.amrs.payment;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService{

	@Autowired
	private PaymentDAO paymentDAO;

	@Override
	public void registerPayment(PaymentDTO paymentDTO) {
		paymentDAO.insertPayment(paymentDTO);
	}


	@Override
	public int getTotalTicketCount(String localId) {
		Integer totalCount = paymentDAO.selectTotalTicketCnt(localId);
		if(totalCount == null) {
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
			
	        // 예외를 발생시키거나 기본값을 설정하여 null 상황을 처리합니다.
	        //throw new IllegalStateException("해당 LOCAL_ID에 대한 TOTAL_CNT 값이 설정되지 않았습니다.");
	    } 
		
		int restCnt = restCount;
		
		return restCnt;
	}

	@Override
	public List<Map<String, Object>> getPaymentList(String memberId) {
		return paymentDAO.selectPaymentList(memberId);
	}

	@Override
	public PaymentDTO getPaymentInfo(String memberId) {
		return paymentDAO.selectPaymentInfo(memberId);
	}



}
