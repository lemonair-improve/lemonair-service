package com.hanghae.lemonairservice.exception.point;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class NotEnoughPointException extends ExpectedException {
	public NotEnoughPointException(String message) {
		super(HttpStatus.PAYMENT_REQUIRED, "레몬이 부족합니다. 남은 레몬 : " +message);
	}
}
