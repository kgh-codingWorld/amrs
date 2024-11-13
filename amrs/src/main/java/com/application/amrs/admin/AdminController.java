package com.application.amrs.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.application.amrs.payment.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private PaymentService paymentService;
	
	@GetMapping("/dashboard")
	public String dashboard(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String adminId = (String) session.getAttribute("adminId");
		
		if (adminId == null) {
	        return "redirect:/member/login";
	    }
		
		model.addAttribute("adminId", adminId);
		System.out.println(session.getAttribute("adminId"));
		return "admin/dashboard";
	}
}
