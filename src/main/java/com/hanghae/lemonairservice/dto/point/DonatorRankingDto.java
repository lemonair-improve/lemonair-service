package com.hanghae.lemonairservice.dto.point;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DonatorRankingDto {
	private long donatorId;
	private int sumOfPoint;
	private String nickname;
}
