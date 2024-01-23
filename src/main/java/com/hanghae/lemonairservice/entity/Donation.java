package com.hanghae.lemonairservice.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.hanghae.lemonairservice.dto.point.DonationRequestDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("donation")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Donation {
	@Id
	private Long id;
	private Long streamerId;
	private Long donatorId;
	private String contents;
	private Integer donatePoint;
	private Timestamp donatedAt;

	public Donation(Long streamerId, Long donatorId, DonationRequestDto donationRequestDto){
		this.streamerId = streamerId;
		this.donatorId = donatorId;
		this.contents = donationRequestDto.getContents();
		this.donatePoint = donationRequestDto.getDonatePoint();
	}
}
