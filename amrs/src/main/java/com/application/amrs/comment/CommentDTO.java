package com.application.amrs.comment;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CommentDTO {

	private int commentId;
	private String commentContent;
	private int commentCnt;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date createDt;
	private Date updateDt;
	private int communityId;
	private String memberId;
	
	// 임시 필드
	private String memberNm;
}
