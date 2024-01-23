package com.hanghae.lemonairservice.dto.point;

import lombok.Getter;

@Getter
public class DonationResponseDto {
	private int remainingPoint;

	public DonationResponseDto(int remainingPoint) {
		this.remainingPoint = remainingPoint;
	}
}
