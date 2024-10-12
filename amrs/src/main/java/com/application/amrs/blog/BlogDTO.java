package com.application.amrs.blog;


import java.util.Date;

import lombok.Data;

@Data
public class BlogDTO {

	private int blogId;
	private String blogOriginalThumbnailName;
	private String blogThumbnailUUID;
	private String blogTitle;
	private String blogContent;
	private int likeCount;
	private int readCnt;
	private Date createDt;
	private Date updateDt;
	private String memberId;
}
