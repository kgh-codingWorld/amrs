package com.application.amrs.member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class MemberServiceImpl implements MemberService {

	@Value("${file.repo.path}")
	private String fileRepositoryPath;
	
	@Value("${iamport.api_key}")
	private String iamportApiKey;
	
	@Value("${iamport.api_secret}")
	private String iamportApiSecret;
	
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
			
			uploadProfile.transferTo(new File(fileRepositoryPath + uploadFile));
		} else {
			
		}
		
		if(memberDTO.getSmsstsYn() == null) memberDTO.setSmsstsYn("n");
		if(memberDTO.getEmailstsYn() == null) memberDTO.setEmailstsYn("n");
		
		memberDTO.setPasswd(passwordEncoder.encode(memberDTO.getPasswd()));
		
		memberDAO.insertMember(memberDTO);
	}

	@Override
	public String checkDuplicatedId(String memberId) {
		
		String isDuplicatedId = "n";
		if(memberDAO.findMemberId(memberId) == null) { // 중복되는 아이디가 없으면
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
				log.warn("로그인 실패: 비밀번호 오류", memberDTO.getMemberId());
			    return false;
			}
		} else {
			log.warn("로그인 실패: 비회원", memberDTO.getMemberId());
		    return false;
		}
	}

	@Override
	public MemberDTO getMemberDetail(String memberId) {
		return memberDAO.selectOneMember(memberId);
	}


	@Override
	public boolean isValidPasswd(String passwd, String memberId) {
		
		String validateData = memberDAO.isValidPasswd(memberId);

		// 기존 DB의 passwd와 일치 시 true 반환
		if(validateData != null) {
			if(passwordEncoder.matches(passwd, validateData)) {
				return true;
			} else {
			    return false;
			}
		} else {
		    return false;
		}
		
	}

	@Override
	public String modifyMyPasswd(String newPasswd, String memberId) {
		MemberDTO memberDetail = memberDAO.selectOneMember(memberId);
		if(memberDetail == null) {
			throw new RuntimeException("회원 정보가 존재하지 않습니다.");
		}
		memberDetail.setPasswd(passwordEncoder.encode(newPasswd));
		int updateRow = memberDAO.updateMyPasswd(memberDetail);
		if(updateRow == 0) {
			throw new RuntimeException("비밀번호 변경 실패.");
		}
		return "비밀번호 변경 완료";
	}
	
	@Override
	public String modifyMemberHp(String newMemberHp, String memberId) {
		MemberDTO memberDetail = memberDAO.selectOneMember(memberId);
		if(memberDetail == null) {
			throw new RuntimeException("회원 정보가 존재하지 않습니다.");
		}
		memberDetail.setMemberHp(newMemberHp);
		int updateRow = memberDAO.updateMemberHp(memberDetail);
		return "휴대폰 번호 변경 완료";
	}

	@Override
	public void modifyMyInfo(MultipartFile uploadProfile, MemberDTO memberDTO)
			throws IllegalStateException, IOException {
		if(!uploadProfile.isEmpty()) {
			
			new File(fileRepositoryPath + memberDTO.getMemberProfileUUID()).delete();
			
			String originalFilename = uploadProfile.getOriginalFilename();
			memberDTO.setMemberProfileOriginalName(originalFilename);
			
			String extension = originalFilename.substring(originalFilename.indexOf("."));
			
			String uploadFile = UUID.randomUUID() + extension;
			memberDTO.setMemberProfileUUID(uploadFile);
			
			uploadProfile.transferTo(new File(fileRepositoryPath + uploadFile));
		}
		
		if(memberDTO.getSmsstsYn() == null) memberDTO.setSmsstsYn("n");
		if(memberDTO.getEmailstsYn() == null) memberDTO.setEmailstsYn("n");
		
		memberDAO.updateMyInfo(memberDTO);
		
	}

	@Override
    public boolean removeAccount(String passwd, String memberId) {
        MemberDTO member = memberDAO.login(memberId);

        if (member != null && passwordEncoder.matches(passwd, member.getPasswd())) {
            memberDAO.deleteAccount(memberId);
            return true;  // 탈퇴 성공
        } else {
            // 비밀번호 불일치
            return false;
        }
    }

	@Override
	public MemberDTO getMemberId(String memberId) {
		return memberDAO.findMemberId(memberId);
	}
	
	public String maskLastCharacter(String memberNm) {
	    if (memberNm != null && memberNm.length() > 1) {
	        return memberNm.substring(0, memberNm.length() - 1) + "*";
	    }
	    return memberNm;
	}


	@Override
	public String getMemberNameById(String memberId) {
		return memberDAO.selectMemberNameById(memberId);
	}

}
