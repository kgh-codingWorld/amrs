package com.application.amrs.community;

import java.util.List;
import java.util.Map;

public interface CommunityService {

	public void registerCommunity(CommunityDTO communityDTO);
	public List<Map<String, Object>> getCommunityList(Integer limit);
//	public List<Map<String, Object>> getRecentCommunityList(int count);
	public Map<String, Object> getCommunityById(int communityId, boolean isIncreaseReadCnt);
	public void likePost(int communityId, int likeCount);
	public boolean hasMemberLikedPost(String memberId, int communityId);
	public int toggleLike(String memberId, int communityId, boolean liked);
	public void modifyCommunity(CommunityDTO communityDTO);
	public void removeCommunity(int communityId);
	public List<Map<String, Object>> getMyCommunityList(String memberId);
}
