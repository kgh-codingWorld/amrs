package com.application.amrs.forum;


import java.util.Date;

import lombok.Data;

@Data
public class ForumDTO {

	private int forumId;
	private String forumOriginalThumbnailName;
	private String forumThumbnailUUID;
	private String forumTitle;
	private String forumContent;
	private int likeCount;
	private int readCnt;
	private Date createDt;
	private Date updateDt;
	private String memberId;
}
