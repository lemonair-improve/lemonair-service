package com.hanghae.lemonairservice.exception.point;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class DonationSaveException extends ExpectedException {
	public DonationSaveException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "도네이션 중 오류가 발생했습니다. 다시 시도해 주세요");
	}
}
