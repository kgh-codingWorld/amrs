package com.application.amrs.likePost;

import lombok.Data;

@Data
public class LikePostDTO {

	private int likePostId;
	private int blogId;
	private String memberId;
	private boolean liked;
}
