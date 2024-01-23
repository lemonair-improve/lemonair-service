package com.hanghae.lemonairservice.exception.point;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class PointChargeException extends ExpectedException {
	public PointChargeException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "레몬 충전에 오류가 발생했습니다.");
	}
}
