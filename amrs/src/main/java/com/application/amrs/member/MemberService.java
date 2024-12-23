package com.application.amrs.member;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
	
	public void registerMember(MultipartFile uploadProfile, MemberDTO memberDTO) throws IllegalStateException, IOException;
	public String checkDuplicatedId(String memberId);
	public boolean login(MemberDTO memberDTO);
	public MemberDTO getMemberDetail(String memberId);
	//public boolean isValidPasswd(String passwd, MemberDTO memberDTO);
	public boolean isValidPasswd(String passwd, String memberId);
	public void modifyMyPasswd(String newPasswd, MemberDTO memberDTO);
	public void modifyMyInfo(MultipartFile uploadProfile, MemberDTO memberDTO) throws IllegalStateException, IOException;
	public boolean removeAccount(String passwd, String memberId);
	
	public MemberDTO getMemberId(String username);
	public String getPortOneAccessToken();
	public String maskLastCharacter(String memberNm);
	public String getMemberNameById(String memberId);
}
