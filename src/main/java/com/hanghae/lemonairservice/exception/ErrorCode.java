package com.hanghae.lemonairservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
	EmailAlreadyExists(HttpStatus.CONFLICT, "A001", "사용중인 이메일입니다."),
	LoginIdAlreadyExists(HttpStatus.CONFLICT, "A002", "사용중인 아이디입니다."),
	NicknameAlreadyExists(HttpStatus.CONFLICT, "A003", "사용중인 닉네임입니다."),
	MemberCreateFailed(HttpStatus.INTERNAL_SERVER_ERROR, "A004", "회원가입에 실패했습니다."),
	ChannelCreateFailed(HttpStatus.INTERNAL_SERVER_ERROR, "A005", "회원가입에 실패했습니다."),
	PointCreateFailed(HttpStatus.INTERNAL_SERVER_ERROR, "A006", "회원가입에 실패했습니다."),
	MemberNotFound(HttpStatus.NOT_FOUND, "B001", "회원 정보를 찾을 수 없습니다."),
	ChannelNotFound(HttpStatus.NOT_FOUND, "B002", "해당 방송을 찾을 수 없습니다."),
	NoOnAirChannel(HttpStatus.OK, "C001", "현재 진행중인 생방송이 없습니다."),
	BroadcastEnded(HttpStatus.BAD_REQUEST, "D001", "방송이 종료되었습니다."),
	NotEnoughPoint(HttpStatus.BAD_REQUEST, "E001", "레몬이 부족합니다."),
	DonationFailed(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "도네이션 중 오류가 발생했습니다."),
	PointQueryFailed(HttpStatus.INTERNAL_SERVER_ERROR, "E003", "잔액 조회중 오류가 발생했습니다."),
	PointChargeFailed(HttpStatus.INTERNAL_SERVER_ERROR, "E004", "레몬 충전 중 오류가 발생했습니다.");

	private final HttpStatusCode status;
	private final String code;
	private final String message;
}