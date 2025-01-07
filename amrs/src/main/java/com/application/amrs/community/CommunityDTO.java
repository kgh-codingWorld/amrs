package com.application.amrs.community;


import java.util.Date;

import lombok.Data;

@Data
public class CommunityDTO {

	private int communityId;
	private String communityOriginalThumbnailName;
	private String communityThumbnailUUID;
	private String communityTitle;
	private String communityContent;
	private int likeCount;
	private int readCnt;
	private Date createDt;
	private Date updateDt;
	private String memberId;
	
	public CommunityDTO() {}
	
	public CommunityDTO(int communityId, int likeCount) {
		this.communityId = communityId;
		this.likeCount = likeCount;
	}
}
