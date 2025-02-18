package com.application.amrs.community;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		return processCommunityList(communityDAO.selectCommunityList());
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
		communityDAO.updateLikeCount(new CommunityDTO(communityId, likeCount));
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
	    return communityDAO.checkMemberLike(createParams(memberId, communityId));
	}
	
	@Override
	@Transactional
    public int toggleLike(String memberId, int communityId, boolean liked) {
		Map<String, Object> params = createParams(memberId, communityId);
        boolean hasLiked = communityDAO.checkMemberLike(params); // 좋아요 여부 체크

        if(liked && !hasLiked) { // 좋아요가 존재하지 않으면
    		communityDAO.insertLike(params);
        } else if (!liked && hasLiked){
    		communityDAO.deleteLike(params);
        }
        
        communityDAO.updateLikeCount(new CommunityDTO(communityId, 0));
        return communityDAO.countLikesForCommunity(communityId); // 최종 좋아요 개수 반환
    }

	@Override
	public List<Map<String, Object>> getMyCommunityList(String memberId) {
		return processCommunityList(communityDAO.selectMyCommunityList(memberId), memberId);
	}

	@Override
	public List<Map<String, Object>> getRecentCommunityList(int count) {
		List<Map<String, Object>> allCommunityList = getCommunityList();
		if(allCommunityList.isEmpty()) {
    		return Collections.emptyList();
    	}
    	Collections.shuffle(allCommunityList);
    	return allCommunityList.subList(0, Math.min(count, allCommunityList.size()));
	}
	
	private List<Map<String, Object>> processCommunityList(List<Map<String, Object>> communityList) {
        communityList.forEach(this::maskMemberName);
        return communityList;
    }
	
	private List<Map<String, Object>> processCommunityList(List<Map<String, Object>> communityList, String memberId) {
		communityList.forEach(community -> maskMemberName(community, memberId));
        return communityList;
    }
	
	private void maskMemberName(Map<String, Object> community) {
        String memberId = (String) community.get("memberId");
        if (memberId != null) {
            String memberNm = memberService.getMemberNameById(memberId);
            if (memberNm != null) {
                community.put("memberNm", memberService.maskLastCharacter(memberNm));
            }
        }
    }
	
    private void maskMemberName(Map<String, Object> community, String memberId) {
    	String memberNm = memberService.getMemberNameById(memberId);
        if (memberNm != null) {
            community.put("memberNm", memberService.maskLastCharacter(memberNm));
        }
    }

    private Map<String, Object> createParams(String memberId, int communityId) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("communityId", communityId);
        return params;
    }
}
