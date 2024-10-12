package com.application.amrs.follow;

import lombok.Data;

@Data
public class FollowDTO {

	private String fromMemberId; // 팔로우 하는 사람
	private String toMemberId;	 // 팔로우 받는 사람
}
