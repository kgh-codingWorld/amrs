package com.application.amrs.blog;

import java.util.HashMap;
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
	public void likePost(int blogId, int likeCount) {
		
		Map<String, Object> blogMap = blogDAO.selectBlogById(blogId);
		
		BlogDTO blogDTO = new BlogDTO();
		blogDTO.setBlogId((Integer) blogMap.get("blogId"));
		blogDTO.setLikeCount((Integer) blogMap.get("likeCount"));
	    
		blogDTO.setLikeCount(likeCount);
		blogDAO.updateLikeCount(blogDTO);
	    
	}

	@Override
	public void modifyBlog(BlogDTO blogDTO) {
		blogDAO.updateBlog(blogDTO);
	}

	@Override
	public void removeBlog(int blogId) {
		blogDAO.deleteBlog(blogId);
	}

	@Override
	public boolean hasMemberLikedPost(String memberId, int blogId) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("memberId", memberId);
	    params.put("blogId", blogId);
	    return blogDAO.checkMemberLike(params);
	}
	
	@Override
	@Transactional
    public int toggleLike(String memberId, int blogId, boolean liked) {
		Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("blogId", blogId);

        boolean hasLiked = blogDAO.checkMemberLike(params); // 좋아요 여부 체크

        if(liked) {
        	if (!hasLiked) {
                // 좋아요가 존재하지 않으면
        		blogDAO.insertLike(params);
                System.out.println("insertLike 실행 확인");
            }
        } else {
        	if (hasLiked) {
                // 좋아요가 존재하면
        		blogDAO.deleteLike(params);
                System.out.println("deleteLike 실행 확인");
            }
        }
        
        blogDAO.updateLikeCount(new BlogDTO(blogId, 0));
        
        return blogDAO.countLikesForBlog(blogId); // 최종 좋아요 개수 반환
    }

	@Override
	public List<Map<String, Object>> getMyBlogList(String memberId) {
		
		List<Map<String, Object>> myBlogList = blogDAO.selectMyBlogList(memberId);
		
		for(Map<String, Object> myBlog : myBlogList) {
			//String memberId = (String) myForum.get("memberId");
			String memberNm = memberService.getMemberNameById(memberId);
			String maskedMemberNm = memberService.maskLastCharacter(memberNm);
			myBlog.put("memberNm", maskedMemberNm);
		}
		
		return myBlogList;
	}
}
