package com.application.amrs.member;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDAO {
	
	public void insertMember(MemberDTO memberDTO);
	public String selectDuplicatedId(String memberId);
	public MemberDTO login(String memberId);
	public MemberDTO selectOneMember(String memberId);
}
