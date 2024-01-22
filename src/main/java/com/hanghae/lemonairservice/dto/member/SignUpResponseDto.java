package com.hanghae.lemonairservice.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class SignUpResponseDto {
	private String streamKey;

	public SignUpResponseDto(String streamKey) {
		this.streamKey = streamKey;
	}
}
