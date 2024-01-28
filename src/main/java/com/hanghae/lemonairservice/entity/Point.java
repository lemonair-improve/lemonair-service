package com.hanghae.lemonairservice.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.hanghae.lemonairservice.dto.point.ChargePointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table("point")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {
	@Id
	private Long id;
	private Long memberId;
	private Long donatorId;
	private Boolean charge;
	private Integer point;

	@CreatedDate
	private LocalDateTime createdAt;

	public Point(ChargePointRequestDto chargePointRequestDto, Long memberId) {
		this.memberId = memberId;
		this.donatorId = null;
		this.charge = true;
		this.point = chargePointRequestDto.getPoint();
	}

	public Point(DonationRequestDto donationRequestDto, Long memberId, Long donatorId) {
		this.memberId = memberId;
		this.donatorId = donatorId;
		this.charge = false;
		this.point = donationRequestDto.getDonatePoint();
	}
}