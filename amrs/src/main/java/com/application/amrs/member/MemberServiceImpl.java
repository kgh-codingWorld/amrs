package com.application.amrs.member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class MemberServiceImpl implements MemberService {

	@Value("${file.repo.path}")
	private String fileRepositoryPath;
	
	@Value("${iamport.api_key}")
	private String impKey;
	
	@Value("${iamport.api_secret}")
	private String impSecret;
	
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
		if(memberDAO.findMemberId(memberId) == null) {
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


	@Override
	public boolean isValidPasswd(String passwd, MemberDTO memberDTO) {

		MemberDTO validateData = memberDAO.isValidPasswd(memberDTO.getMemberId());
		
		if(validateData != null) {
			if(passwordEncoder.matches(passwd, validateData.getPasswd())) {
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
	public void modifyMyPasswd(String newPasswd, MemberDTO memberDTO) {
		
		memberDTO.setPasswd(passwordEncoder.encode(newPasswd));
		
		memberDAO.updateMyPasswd(memberDTO);
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
        // 1. 데이터베이스에서 사용자의 정보 조회
        MemberDTO member = memberDAO.login(memberId);

        // 2. 비밀번호 검증 (입력된 비밀번호와 저장된 비밀번호 비교)
        if (member != null && passwordEncoder.matches(passwd, member.getPasswd())) {
            // 3. 비밀번호가 일치하면 회원 탈퇴 처리
            memberDAO.deleteAccount(memberId);
            return true;  // 탈퇴 성공
        } else {
            // 비밀번호 불일치
            return false;
        }
    }

	@Override
	public String getMemberId(String memberId) {
		return memberDAO.findMemberId(memberId);
	}
	
	public String maskLastCharacter(String memberNm) {
	    if (memberNm != null && memberNm.length() > 1) {
	        return memberNm.substring(0, memberNm.length() - 1) + "*";
	    }
	    return memberNm;
	}

	@Override
	@Transactional
	public String getPortOneAccessToken() {
		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = "https://api.iamport.kr/users/getToken";
		
		Map<String, String> iamportKey = new HashMap<>();
	    iamportKey.put("imp_key", impKey); // REST API key
	    iamportKey.put("imp_secret", impSecret); // REST API secret

	    ResponseEntity<Object> responseData = restTemplate.postForEntity(requestUrl, iamportKey, Object.class);
	    LinkedHashMap responseBody = (LinkedHashMap) responseData.getBody();
	    LinkedHashMap responseBodyProps = (LinkedHashMap) responseBody.get("response");
	    String accessToken = (String) responseBodyProps.get("access_token");
		
		return accessToken;
	}

	@Override
	public String getMemberNameById(String memberId) {
		return memberDAO.selectMemberNameById(memberId);
	}


}
