package com.application.amrs.member;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
	
	public void registerMember(MultipartFile uploadProfile, MemberDTO memberDTO) throws IllegalStateException, IOException;
	public String checkDuplicatedId(String memberId);
	public boolean login(MemberDTO memberDTO);
	public MemberDTO getMemberDetail(String memberId);
}
