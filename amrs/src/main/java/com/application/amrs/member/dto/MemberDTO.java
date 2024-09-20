package com.application.amrs.member.dto;

import java.util.Date;

import com.application.amrs.member.enums.Role;

import lombok.Data;

@Data
public class MemberDTO {
	
	private int memberId;
	private String email;
	private String passwd;
	private Date birthAt;
	private String memberPhoneNumber;
	private String memberProfileOriginalName;
	private String memberProfileUUID;
	private String smsstsYn;
	private String emailstsYn;
	private String zipcode;
	private String roadAddress;
	private String jibunAddress;
	private String namujiAddress;
	private int memberPoint;
	private Role memberRole;
	private Date createDt;
	private Date updateDt;
	
}
