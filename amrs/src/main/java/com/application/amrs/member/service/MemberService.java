package com.application.amrs.member.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.application.amrs.member.dto.MemberDTO;

public interface MemberService {
	
	public void createMember(MultipartFile uploadProfile, MemberDTO memberDTO) throws IllegalStateException, IOException;
}
