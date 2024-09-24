package com.application.amrs.member;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.application.amrs.enums.Role;

import lombok.Data;

@Data
public class MemberDTO {
	
	private String memberId;
	private String passwd;
	private String memberNm;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date birthAt;
	private String memberHp;
	private String memberProfileOriginalName;
	private String memberProfileUUID;
	private String smsstsYn;
	private String emailstsYn;
	private String zipcode;
	private String roadAddress;
	private String jibunAddress;
	private String namujiAddress;
	private Role memberRole;
	private int memberPoint;
	private Date createDt;
	private Date updateDt;
	
}
