package com.application.amrs.forum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.amrs.member.MemberService;

@Service
public class ForumServiceImpl implements ForumService {

	@Autowired
	private ForumDAO forumDAO;
	
	@Autowired
	private MemberService memberService;

	@Override
	public List<Map<String, Object>> getForumList() {
		
		List<Map<String, Object>> forumList = forumDAO.selectForumList();
		
		for(Map<String, Object> forum : forumList) {
			String memberId = (String) forum.get("memberId");
			String memberNm = memberService.getMemberNameById(memberId);
			String maskedMemberNm = memberService.maskLastCharacter(memberNm);
			forum.put("memberNm", maskedMemberNm);
		}
		
		return forumList; // 블로그 리스트 조회
	}
	
	@Override
	public void registerForum(ForumDTO forumDTO) {
		forumDAO.insertForum(forumDTO); // 블로그 포스팅
	}

	@Override
	@Transactional
	public Map<String, Object> getForumById(int forumId, boolean isIncreaseReadCnt) {
		if(isIncreaseReadCnt) {
			forumDAO.updateReadCnt(forumId);
		}
		return forumDAO.selectForumById(forumId);
	}

	@Override
	public void likePost(int forumId, int likeCount) {
		
		Map<String, Object> forumMap = forumDAO.selectForumById(forumId);
		
		ForumDTO forumDTO = new ForumDTO();
		forumDTO.setForumId((Integer) forumMap.get("forumId"));
		forumDTO.setLikeCount((Integer) forumMap.get("likeCount"));
	    
		forumDTO.setLikeCount(likeCount);
		forumDAO.updateLikeCount(forumDTO);
	    
	}

	@Override
	public void modifyForum(ForumDTO forumDTO) {
		forumDAO.updateForum(forumDTO);
	}

	@Override
	public void removeForum(int forumId) {
		forumDAO.deleteForum(forumId);
	}

	@Override
	public boolean hasMemberLikedPost(String memberId, int forumId) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("memberId", memberId);
	    params.put("forumId", forumId);
	    return forumDAO.checkMemberLike(params);
	}
}
