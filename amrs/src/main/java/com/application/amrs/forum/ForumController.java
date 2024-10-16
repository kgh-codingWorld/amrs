package com.application.amrs.forum;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/forum")
public class ForumController {
	
	@Value("${file.repo.path}")
	private String fileRepositoryPath; 

	@Autowired
	private ForumService forumService;
	
	 @PostMapping("/registerForum")
	    public String registerforum(@ModelAttribute ForumDTO forumDTO,
//	    		@RequestParam("memberId") String memberId, 
//	    							@RequestParam("forumTitle") String forumTitle,
//	    							@RequestParam("forumContent") String forumContent,
	    		@RequestParam(value = "thumbnail", required = false) MultipartFile uploadProfile) throws IOException {
		 
//		 ForumDTO forumDTO = new ForumDTO();
//		 forumDTO.setMemberId(memberId);
//		 forumDTO.setForumTitle(forumTitle);
//		 forumDTO.setForumContent(forumContent);
		 
		 forumDTO.setMemberId(forumDTO.getMemberId());
		 forumDTO.setForumTitle(forumDTO.getForumTitle());
		 forumDTO.setForumContent(forumDTO.getForumContent());
		 
		 if(!uploadProfile.isEmpty()) {
			 String originalFilename = uploadProfile.getOriginalFilename();
			 forumDTO.setForumOriginalThumbnailName(originalFilename);
			 
			 String extension = originalFilename.substring(originalFilename.indexOf("."));
			 String uploadFile = UUID.randomUUID() + extension;
			 forumDTO.setForumThumbnailUUID(uploadFile);
			 
			 uploadProfile.transferTo(new File(fileRepositoryPath + uploadFile));
		 }

	        // 블로그 포스트 등록 서비스 호출
	        forumService.registerForum(forumDTO);

	        // 성공적으로 등록되었으면 블로그 리스트 페이지로 리다이렉트
	        return "redirect:/forum/myForum";
	    }
	
	@PostMapping("/modifyForum")
	public String modifyForum(@RequestParam("memberId") String memberId,
							  @RequestParam("forumId") int forumId,
							  @RequestParam("forumTitle") String forumTtile,
							  @RequestParam("forumContent") String forumContent,
			@RequestParam(value = "thumbnail", required = false) MultipartFile uploadProfile) throws IllegalStateException, IOException {
		
		ForumDTO forumDTO = new ForumDTO();
		forumDTO.setMemberId(memberId);
		forumDTO.setForumId(forumId);
		forumDTO.setForumTitle(forumTtile);
		forumDTO.setForumContent(forumContent);
		
		if(!uploadProfile.isEmpty()) {
			 String originalFilename = uploadProfile.getOriginalFilename();
			 forumDTO.setForumOriginalThumbnailName(originalFilename);
			 
			 String extension = originalFilename.substring(originalFilename.indexOf("."));
			 String uploadFile = UUID.randomUUID() + extension;
			 forumDTO.setForumThumbnailUUID(uploadFile);
			 
			 uploadProfile.transferTo(new File(fileRepositoryPath + uploadFile));
		 }
		forumService.modifyForum(forumDTO);
		return "redirect:/forum/forumMain";
	}
	
}
