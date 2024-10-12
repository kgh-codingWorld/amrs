package com.application.amrs.exception;

import com.application.amrs.enums.ErrorCode;

public class FollowException extends RuntimeException {
	private final ErrorCode errorCode;
	
	public FollowException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
