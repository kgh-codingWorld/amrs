package com.application.amrs.member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

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
	public boolean isValidPasswd(String passwd, String memberId) {

		//MemberDTO validateData = memberDAO.isValidPasswd(memberDTO.getMemberId());
		String validateData = memberDAO.isValidPasswd(memberId);
		
		// 기존 DB의 passwd와 일치 시 true 반환
		if(validateData != null) {
			if(passwordEncoder.matches(passwd, validateData)) {
				return true;
			} else {
				//log.warn("로그인 실패", memberDTO.getMemberId());
			    return false;
			}
		} else {
			//log.warn("로그인 실패", memberDTO.getMemberId());
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
	@Transactional
	public String getPortOneAccessToken() {
	    RestTemplate restTemplate = new RestTemplate();
	    
	    String tokenUrl = "https://api.iamport.kr/users/getToken";

	    // 요청 본문에 API 키와 시크릿을 포함
	    Map<String, String> tokenRequest = new HashMap<>();
	    tokenRequest.put("imp_key", iamportApiKey);
	    tokenRequest.put("imp_secret", iamportApiSecret);

	    // Content-Type을 JSON으로 설정
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    // 요청 본문과 헤더를 포함하여 HttpEntity 생성
	    HttpEntity<Map<String, String>> entity = new HttpEntity<>(tokenRequest, headers);

	    // POST 요청 전송
	    try {
	        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, entity, Map.class);
	        Map<String, Object> response = tokenResponse.getBody();

	        // 응답에서 액세스 토큰을 추출
	        if (response != null && response.get("response") != null) {
	            Map<String, Object> responseData = (Map<String, Object>) response.get("response");
	            return (String) responseData.get("access_token");
	        } else {
	            throw new RuntimeException("포트원 액세스 토큰을 발급받을 수 없습니다.");
	        }
	    } catch (HttpClientErrorException e) {
	        System.err.println("HttpClientErrorException 발생: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
	        throw new RuntimeException("포트원 API 요청 중 오류 발생: " + e.getMessage());
	    } catch (Exception e) {
	        System.err.println("Exception 발생: " + e.getMessage());
	        throw new RuntimeException("서버 오류: " + e.getMessage());
	    }
	}

	@Override
	public String getMemberNameById(String memberId) {
		return memberDAO.selectMemberNameById(memberId);
	}


}
