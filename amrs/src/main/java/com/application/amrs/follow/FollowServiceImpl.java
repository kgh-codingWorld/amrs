package com.application.amrs.follow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.amrs.enums.ErrorCode;
import com.application.amrs.exception.FollowException;

@Service
public class FollowServiceImpl implements FollowService {

	@Autowired
    private FollowDAO followDAO;
	
	@Override
	public void follow(String fromMemberId, String toMemberId) {
		
		// 자기 자신을 팔로우할 수 없음
        if (fromMemberId.equals(toMemberId)) {
            throw new FollowException(ErrorCode.INVALID_REQUEST, "자기 자신을 팔로우할 수 없습니다.");
        }

        // 이미 팔로우한 경우 중복 팔로우 방지
        if (followDAO.findFollow(fromMemberId, toMemberId) != null) {
            throw new FollowException(ErrorCode.FOLLOW_DUPLICATED, "이미 팔로우한 사용자입니다.");
        }

        // 팔로우 추가
        followDAO.insertFollow(fromMemberId, toMemberId);

	}

	@Override
	@Transactional
	public void cancelFollow(String fromMemberId, String toMemberId) {
		followDAO.deleteFollow(fromMemberId, toMemberId);
	}

	@Override
	public List<FollowDTO> getFollowingList(String fromMemberId) {
		return followDAO.getFollowingList(fromMemberId);
	}

	@Override
	public List<FollowDTO> getFollowersList(String toMemberId) {
		// TODO Auto-generated method stub
		return followDAO.getFollowersList(toMemberId);
	}

}
