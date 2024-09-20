package com.application.amrs.member.dao;

import org.apache.ibatis.annotations.Mapper;

import com.application.amrs.member.dto.MemberDTO;

@Mapper
public interface MemberDAO {
	
	public void insertMember(MemberDTO memberDTO);
}
