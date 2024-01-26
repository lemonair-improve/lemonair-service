package com.hanghae.lemonairservice.dto.member;

import lombok.Getter;

@Getter
public class SignUpResponseDto {
	private String streamKey;

	public SignUpResponseDto(String streamKey) {
		this.streamKey = streamKey;
	}
}
