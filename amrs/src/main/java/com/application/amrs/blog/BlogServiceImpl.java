package com.application.amrs.blog;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.amrs.member.MemberService;

@Service
public class BlogServiceImpl implements BlogService {

	@Autowired
	private BlogDAO blogDAO;
	
	@Autowired
	private MemberService memberService;

	@Override
	public List<Map<String, Object>> getBlogList() {
		
		List<Map<String, Object>> blogList = blogDAO.selectBlogList();
		
		for(Map<String, Object> blog : blogList) {
			String memberId = (String) blog.get("memberId");
			String memberNm = memberService.getMemberNameById(memberId);
			String maskedMemberNm = memberService.maskLastCharacter(memberNm);
			blog.put("memberNm", maskedMemberNm);
		}
		
		return blogList; // 블로그 리스트 조회
	}
	
	@Override
	public void registerBlog(BlogDTO blogDTO) {
		blogDAO.insertBlog(blogDTO); // 블로그 포스팅
	}

	@Override
	@Transactional
	public Map<String, Object> getBlogById(int blogId, boolean isIncreaseReadCnt) {
		if(isIncreaseReadCnt) {
			blogDAO.updateReadCnt(blogId);
		}
		return blogDAO.selectBlogById(blogId);
	}

	@Override
	public int likePost(int blogId, int likeCount) {
		
		Map<String, Object> blogDTO = blogDAO.selectBlogById(blogId);
	    int updatedLikeCount = ((BlogDTO) blogDTO).getLikeCount() + 1;
	    blogDAO.updateLikeCount(blogId, updatedLikeCount);
	    
	    return updatedLikeCount;
	}

	@Override
	public void modifyBlog(BlogDTO blogDTO) {
		blogDAO.updateBlog(blogDTO);
	}

	@Override
	public void removeBlog(int blogId) {
		blogDAO.deleteBlog(blogId);
	}


}
