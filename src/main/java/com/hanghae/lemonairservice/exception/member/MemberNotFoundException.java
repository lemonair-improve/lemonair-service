package com.hanghae.lemonairservice.exception.member;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class MemberNotFoundException extends ExpectedException {
	public MemberNotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}

	public MemberNotFoundException(Long memberId) {
		super(HttpStatus.NOT_FOUND, memberId + "회원정보를 찾을 수 없습니다.");
	}
}
