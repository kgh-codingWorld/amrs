package com.application.amrs.blog;

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
@RequestMapping("/blog")
public class BlogController {
	
	@Value("${file.repo.path}")
	private String fileRepositoryPath; 

	@Autowired
	private BlogService blogService;
	
	 @PostMapping("/registerBlog")
	    public String registerBlog(@ModelAttribute BlogDTO blogDTO,
	    							@RequestParam(value = "thumbnail", required = false) MultipartFile uploadProfile) throws IOException {
		 
		 blogDTO.setMemberId(blogDTO.getMemberId());
		 blogDTO.setBlogTitle(blogDTO.getBlogTitle());
		 blogDTO.setBlogContent(blogDTO.getBlogContent());
		 
		 if(!uploadProfile.isEmpty()) {
			 String originalFilename = uploadProfile.getOriginalFilename();
			 blogDTO.setBlogOriginalThumbnailName(originalFilename);
			 
			 String extension = originalFilename.substring(originalFilename.indexOf("."));
			 String uploadFile = UUID.randomUUID() + extension;
			 blogDTO.setBlogThumbnailUUID(uploadFile);
			 
			 uploadProfile.transferTo(new File(fileRepositoryPath + uploadFile));
		 }

	        // 블로그 포스트 등록 서비스 호출
	        blogService.registerBlog(blogDTO);

	        // 성공적으로 등록되었으면 블로그 리스트 페이지로 리다이렉트
	        return "redirect:/blog/myBlogPostList";
	    }
	
	@PostMapping("/modifyBlog")
	public String modifyBlog(@RequestParam("blogId") int blogId,
							  @RequestParam("blogTitle") String blogTitle,
							  @RequestParam("blogContent") String blogContent,
			@RequestParam(value = "thumbnail", required = false) MultipartFile uploadProfile) throws IllegalStateException, IOException {
		
		BlogDTO blogDTO = new BlogDTO();
		blogDTO.setBlogId(blogId);
		blogDTO.setBlogTitle(blogTitle);
		blogDTO.setBlogContent(blogContent);
		
		if(!uploadProfile.isEmpty()) {
			 String originalFilename = uploadProfile.getOriginalFilename();
			 blogDTO.setBlogOriginalThumbnailName(originalFilename);
			 
			 String extension = originalFilename.substring(originalFilename.indexOf("."));
			 String uploadFile = UUID.randomUUID() + extension;
			 blogDTO.setBlogThumbnailUUID(uploadFile);
			 
			 uploadProfile.transferTo(new File(fileRepositoryPath + uploadFile));
		 }
		blogService.modifyBlog(blogDTO);
		return "redirect:/blog/blogMain";
	}
	
	
}
