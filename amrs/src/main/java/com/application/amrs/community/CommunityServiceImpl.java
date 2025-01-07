package com.application.amrs.community;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.amrs.member.MemberService;

@Service
public class CommunityServiceImpl implements CommunityService {

	@Autowired
	private CommunityDAO communityDAO;
	
	@Autowired
	private MemberService memberService;

	@Override
	public List<Map<String, Object>> getCommunityList() {
		
		List<Map<String, Object>> communityList = communityDAO.selectCommunityList();
		
		for(Map<String, Object> community : communityList) {
			String memberId = (String) community.get("memberId");
			String memberNm = memberService.getMemberNameById(memberId);
			String maskedMemberNm = memberService.maskLastCharacter(memberNm);
			community.put("memberNm", maskedMemberNm);
		}
		
		return communityList; // 블로그 리스트 조회
	}
	
	@Override
	public void registerCommunity(CommunityDTO communityDTO) {
		communityDAO.insertCommunity(communityDTO); // 블로그 포스팅
	}

	@Override
	@Transactional
	public Map<String, Object> getCommunityById(int communityId, boolean isIncreaseReadCnt) {
		if(isIncreaseReadCnt) {
			communityDAO.updateReadCnt(communityId);
		}
		return communityDAO.selectCommunityById(communityId);
	}

	@Override
	public void likePost(int communityId, int likeCount) {
		
		Map<String, Object> communityMap = communityDAO.selectCommunityById(communityId);
		
		CommunityDTO communityDTO = new CommunityDTO();
		communityDTO.setCommunityId((Integer) communityMap.get("communityId"));
		communityDTO.setLikeCount((Integer) communityMap.get("likeCount"));
	    
		communityDTO.setLikeCount(likeCount);
		communityDAO.updateLikeCount(communityDTO);
	    
	}

	@Override
	public void modifyCommunity(CommunityDTO communityDTO) {
		communityDAO.updateCommunity(communityDTO);
	}

	@Override
	public void removeCommunity(int communityId) {
		communityDAO.deleteCommunity(communityId);
	}

	@Override
	public boolean hasMemberLikedPost(String memberId, int communityId) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("memberId", memberId);
	    params.put("communityId", communityId);
	    return communityDAO.checkMemberLike(params);
	}
	
	@Override
	@Transactional
    public int toggleLike(String memberId, int communityId, boolean liked) {
		Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("communityId", communityId);

        boolean hasLiked = communityDAO.checkMemberLike(params); // 좋아요 여부 체크

        if(liked) {
        	if (!hasLiked) {
                // 좋아요가 존재하지 않으면
        		communityDAO.insertLike(params);
                System.out.println("insertLike 실행 확인");
            }
        } else {
        	if (hasLiked) {
                // 좋아요가 존재하면
        		communityDAO.deleteLike(params);
                System.out.println("deleteLike 실행 확인");
            }
        }
        
        communityDAO.updateLikeCount(new CommunityDTO(communityId, 0));
        
        return communityDAO.countLikesForCommunity(communityId); // 최종 좋아요 개수 반환
    }

	@Override
	public List<Map<String, Object>> getMyCommunityList(String memberId) {
		
		List<Map<String, Object>> myCommunityList = communityDAO.selectMyCommunityList(memberId);
		
		for(Map<String, Object> myCommunity : myCommunityList) {
			//String memberId = (String) myForum.get("memberId");
			String memberNm = memberService.getMemberNameById(memberId);
			String maskedMemberNm = memberService.maskLastCharacter(memberNm);
			myCommunity.put("memberNm", maskedMemberNm);
		}
		
		return myCommunityList;
	}

	@Override
	public List<Map<String, Object>> getRecentCommunityList(int count) {
		List<Map<String, Object>> allCommunityList = getCommunityList();
		if(allCommunityList.isEmpty()) {
    		System.out.println("커뮤니티 데이터 없음");
    		return Collections.emptyList();
    	}
    	
    	Collections.shuffle(allCommunityList);
    	
    	return allCommunityList.subList(0, Math.min(count, allCommunityList.size()));
	}
}
