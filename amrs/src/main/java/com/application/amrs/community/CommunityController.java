package com.application.amrs.community;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/community")
public class CommunityController {
	
	@Value("${file.repo.path}")
	private String fileRepositoryPath; 

	@Autowired
	private CommunityService communityService;
	
	@PostMapping("/registerCommunity")
    public String registerCommunity(@ModelAttribute CommunityDTO communityDTO,
    							@RequestParam(value = "thumbnail", required = false) MultipartFile uploadProfile) throws IOException {
	 
	 communityDTO.setMemberId(communityDTO.getMemberId());
	 communityDTO.setCommunityTitle(communityDTO.getCommunityTitle());
	 communityDTO.setCommunityContent(communityDTO.getCommunityContent());
	 
	 if(!uploadProfile.isEmpty()) {
		 String originalFilename = uploadProfile.getOriginalFilename();
		 communityDTO.setCommunityOriginalThumbnailName(originalFilename);
		 
		 String extension = originalFilename.substring(originalFilename.indexOf("."));
		 String uploadFile = UUID.randomUUID() + extension;
		 communityDTO.setCommunityThumbnailUUID(uploadFile);
		 
		 uploadProfile.transferTo(new File(fileRepositoryPath + uploadFile));
	 }

        // 블로그 포스트 등록 서비스 호출
	 	communityService.registerCommunity(communityDTO);

        // 성공적으로 등록되었으면 블로그 리스트 페이지로 리다이렉트
        return "redirect:/community/myCommunityPostList";
    }
	 
	 
	
	@PostMapping("/modifyCommunity")
	public String modifyCommunity(
	        @RequestParam("communityId") int communityId,
	        @RequestParam("communityTitle") String communityTitle,
	        @RequestParam("communityContent") String communityContent,
	        @RequestParam(value = "thumbnail", required = false) MultipartFile uploadProfile
	) throws IllegalStateException, IOException {

	    System.out.println("checking1: 기존 데이터 조회");

	    // 기존 게시글 데이터 가져오기
	    Map<String, Object> existingCommunityMap = communityService.getCommunityById(communityId, false);

	    // Map -> CommunityDTO 변환
	    CommunityDTO existingCommunity = new CommunityDTO();
	    existingCommunity.setCommunityId((Integer) existingCommunityMap.get("communityId"));
	    existingCommunity.setCommunityTitle((String) existingCommunityMap.get("communityTitle"));
	    existingCommunity.setCommunityContent((String) existingCommunityMap.get("communityContent"));
	    existingCommunity.setCommunityOriginalThumbnailName((String) existingCommunityMap.get("communityOriginalThumbnailName"));
	    existingCommunity.setCommunityThumbnailUUID((String) existingCommunityMap.get("communityThumbnailUUID"));

	    System.out.println("checking2: 기존 데이터 매핑 완료");

	    // 새로운 DTO 객체 생성
	    CommunityDTO communityDTO = new CommunityDTO();
	    communityDTO.setCommunityId(communityId);
	    communityDTO.setCommunityTitle(communityTitle);
	    communityDTO.setCommunityContent(communityContent);

	    // 파일이 업로드된 경우
	    if (uploadProfile != null && !uploadProfile.isEmpty()) {
	        System.out.println("checking3: 파일 업로드 진행");

	        String originalFilename = uploadProfile.getOriginalFilename();
	        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	        String uploadFile = UUID.randomUUID() + extension;

	        System.out.println("checking4: 새 파일명 = " + uploadFile);

	        // 기존 파일이 존재하고 새 파일이 기존 파일과 다를 경우
	        if (existingCommunity.getCommunityThumbnailUUID() == null || 
	            !existingCommunity.getCommunityThumbnailUUID().equals(uploadFile)) {

	            System.out.println("checking5: 기존 파일 삭제 확인");

	            // 기존 파일 삭제
	            if (existingCommunity.getCommunityThumbnailUUID() != null) {
	                File oldFile = new File(fileRepositoryPath + existingCommunity.getCommunityThumbnailUUID());
	                if (oldFile.exists()) {
	                    System.out.println("checking6: 기존 파일 삭제 중 -> " + oldFile.getName());
	                    oldFile.delete();
	                }
	            }

	            // 새 파일 저장
	            File newFile = new File(fileRepositoryPath + uploadFile);
	            uploadProfile.transferTo(newFile);
	            System.out.println("checking7: 새 파일 저장 완료");

	            // DTO에 새로운 파일 정보 업데이트
	            communityDTO.setCommunityOriginalThumbnailName(originalFilename);
	            communityDTO.setCommunityThumbnailUUID(uploadFile);
	        }
	    } else {
	        System.out.println("checking8: 파일 변경 없음, 기존 파일 유지");
	        // 파일이 변경되지 않았다면 기존 정보 유지
	        communityDTO.setCommunityOriginalThumbnailName(existingCommunity.getCommunityOriginalThumbnailName());
	        communityDTO.setCommunityThumbnailUUID(existingCommunity.getCommunityThumbnailUUID());
	    }

	    // DB 업데이트
	    communityService.modifyCommunity(communityDTO);
	    System.out.println("checking9: 게시글 업데이트 완료");

	    return "redirect:/community/communityMain";
	}

	
	
}

