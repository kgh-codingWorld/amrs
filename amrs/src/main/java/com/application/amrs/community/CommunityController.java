package com.application.amrs.community;

import java.io.File;
import java.io.IOException;
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
	public String modifyCommunity(@RequestParam("communityId") int communityId,
							  @RequestParam("communityTitle") String communityTitle,
							  @RequestParam("communityContent") String communityContent,
			@RequestParam(value = "thumbnail", required = false) MultipartFile uploadProfile) throws IllegalStateException, IOException {
		
		CommunityDTO communityDTO = new CommunityDTO();
		communityDTO.setCommunityId(communityId);
		communityDTO.setCommunityTitle(communityTitle);
		communityDTO.setCommunityContent(communityContent);
		
		if(!uploadProfile.isEmpty()) {
			 String originalFilename = uploadProfile.getOriginalFilename();
			 communityDTO.setCommunityOriginalThumbnailName(originalFilename);
			 
			 String extension = originalFilename.substring(originalFilename.indexOf("."));
			 String uploadFile = UUID.randomUUID() + extension;
			 communityDTO.setCommunityThumbnailUUID(uploadFile);
			 
			 uploadProfile.transferTo(new File(fileRepositoryPath + uploadFile));
		 }
		communityService.modifyCommunity(communityDTO);
		return "redirect:/community/communityMain";
	}
	
	
}

