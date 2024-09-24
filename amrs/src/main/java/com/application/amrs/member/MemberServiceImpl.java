package com.application.amrs.member;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class MemberServiceImpl implements MemberService {

	@Value("${file.repo.path}")
	private String fileRepositoryPath;
	
	@Autowired
	private MemberDAO memberDAO;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void registerMember(MultipartFile uploadProfile, MemberDTO memberDTO) throws IllegalStateException, IOException {
		
		if(!uploadProfile.isEmpty()) {
			String originalFilename = uploadProfile.getOriginalFilename();
			memberDTO.setMemberProfileOriginalName(originalFilename);
			
			String extension = originalFilename.substring(originalFilename.indexOf("."));
			String uploadFile = UUID.randomUUID() + extension;
			memberDTO.setMemberProfileUUID(uploadFile);
			
			uploadProfile.transferTo(new File(fileRepositoryPath + extension));
		}
		
		if(memberDTO.getSmsstsYn() == null) memberDTO.setSmsstsYn("n");
		if(memberDTO.getEmailstsYn() == null) memberDTO.setEmailstsYn("n");
		
		memberDTO.setPasswd(passwordEncoder.encode(memberDTO.getPasswd()));
		
		System.out.println("role: " + memberDTO.getMemberRole());
		System.out.println("ServiceImpl의 memberDTO" + memberDTO);
		
		memberDAO.insertMember(memberDTO);
	}

	@Override
	public String checkDuplicatedId(String memberId) {
		
		String isDuplicatedId = "n";
		if(memberDAO.selectDuplicatedId(memberId) == null) {
			isDuplicatedId = "y";
		}
		return isDuplicatedId;
	}

	@Override
	public boolean login(MemberDTO memberDTO) {
		
		MemberDTO validateData = memberDAO.login(memberDTO.getMemberId());
		
		if(validateData != null) {
			if(passwordEncoder.matches(memberDTO.getPasswd(), validateData.getPasswd())) {
				memberDTO.setMemberNm(validateData.getMemberNm());
				return true;
			} else {
				log.warn("Login failed: incorrect password for memberId - {}", memberDTO.getMemberId());
			    return false;
			}
		} else {
			log.warn("Login failed: memberId not found - {}", memberDTO.getMemberId());
		    return false;
		}
	}

	@Override
	public MemberDTO getMemberDetail(String memberId) {
		return memberDAO.selectOneMember(memberId);
	}


}
