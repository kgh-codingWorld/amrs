//package com.application.amrs.comment;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/comment")
//public class CommentController {
//
//	@Autowired
//	private CommentService commentService;
//	
//	@PostMapping("/registerComment")
//	public String registerComment(@RequestBody CommentDTO commentDTO) {
//		commentService.registerComment(commentDTO);
//		return "redirect:/forum/forumDetail";
//	}
//}
