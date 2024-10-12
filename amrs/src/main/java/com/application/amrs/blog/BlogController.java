package com.application.amrs.blog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

	@Autowired
	private BlogService blogService;
	
	 @PostMapping("/registerBlog")
	    public String registerBlog(
	            @RequestParam("memberId") String memberId,
	            @RequestParam("blogTitle") String blogTitle,
	            @RequestParam("blogContent") String blogContent,
	            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
	            Model model) throws IOException {

	        String originalThumbnailName = null;
	        String thumbnailUUID = null;

	        // 썸네일 파일 처리
	        if (thumbnail != null && !thumbnail.isEmpty()) {
	            originalThumbnailName = thumbnail.getOriginalFilename();
	            String extension = originalThumbnailName.substring(originalThumbnailName.lastIndexOf("."));
	            thumbnailUUID = UUID.randomUUID().toString() + extension;

	            // 파일을 서버 디렉토리에 저장하는 로직
	            String uploadDir = "/path/to/upload/dir";  // 실제 파일 저장 경로
	            File file = new File(uploadDir + "/" + thumbnailUUID);
	            thumbnail.transferTo(file);
	        }

	        // BlogDTO 객체에 값 설정
	        BlogDTO blogDTO = new BlogDTO();
	        blogDTO.setMemberId(memberId);
	        blogDTO.setBlogTitle(blogTitle);
	        blogDTO.setBlogContent(blogContent);
	        blogDTO.setBlogOriginalThumbnailName(originalThumbnailName);
	        blogDTO.setBlogThumbnailUUID(thumbnailUUID);

	        // 블로그 포스트 등록 서비스 호출
	        blogService.registerBlog(blogDTO);

	        // 성공적으로 등록되었으면 블로그 리스트 페이지로 리다이렉트
	        return "redirect:/blog/myBlog";
	    }
	
	@PostMapping("/modifyBlog")
	public String modifyBlog(@ModelAttribute BlogDTO blogDTO) {
		blogService.modifyBlog(blogDTO);
		return "redirect:/blog/blogDetail";
	}
}
