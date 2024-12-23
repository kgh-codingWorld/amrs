package com.application.amrs.replyComment;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class ReplyCommentDTO {

	private int replyId;
	private String replyContent;
	private int commentId;
	private String memberId;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date createDt;
	private Date updateDt;
	
	// 임시필드
	private String memberNm;
}
