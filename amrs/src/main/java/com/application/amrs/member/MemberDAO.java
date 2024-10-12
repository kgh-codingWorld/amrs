package com.application.amrs.member;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDAO {
	
	public void insertMember(MemberDTO memberDTO);
	//public String selectDuplicatedId(String memberId);
	public String findMemberId(String memberId);
	public MemberDTO login(String memberId);
	public MemberDTO selectOneMember(String memberId);
	public MemberDTO isValidPasswd(String memberId);
	public void updateMyPasswd(MemberDTO memberDTO);
	public void updateMyInfo(MemberDTO memberDTO);
	public void deleteAccount(String memberId);
	public String selectMemberNameById(String memberId);
}
