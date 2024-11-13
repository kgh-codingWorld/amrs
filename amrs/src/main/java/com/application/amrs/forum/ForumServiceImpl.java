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
	
	@Override
    public int toggleLike(String memberId, int forumId, boolean liked) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("forumId", forumId);
        params.put("liked", liked);

        boolean hasLiked = forumDAO.checkMemberLike(params); // 좋아요 여부 체크

        if (hasLiked) {
            // 이미 좋아요가 존재하면 상태 업데이트
            forumDAO.updateLikeStatus(params);
            System.out.println("updateLikeStatus 실행 확인");
        } else {
            // 좋아요가 존재하지 않으면 새로 삽입
            forumDAO.insertLike(params);
            System.out.println("insertLike 실행 확인");
        }
        
        int likeCount = forumDAO.countLikesForForum(forumId);
        
        ForumDTO forumDTO = new ForumDTO();
        forumDTO.setForumId(forumId);
        forumDTO.setLikeCount(likeCount);
        forumDAO.updateLikeCount(forumDTO);
        
        return likeCount; // 최종 좋아요 개수 반환
    }

	@Override
	public List<Map<String, Object>> getMyForumList(String memberId) {
		
		List<Map<String, Object>> myForumList = forumDAO.selectMyForumList(memberId);
		
		for(Map<String, Object> myForum : myForumList) {
			//String memberId = (String) myForum.get("memberId");
			String memberNm = memberService.getMemberNameById(memberId);
			String maskedMemberNm = memberService.maskLastCharacter(memberNm);
			myForum.put("memberNm", maskedMemberNm);
		}
		
		return myForumList;
	}
}
