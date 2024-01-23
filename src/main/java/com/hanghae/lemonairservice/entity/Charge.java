package com.hanghae.lemonairservice.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Table("charge")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Charge {
	@Id
	private Long id;
	private Long memberId;
	private int point;
	private Timestamp chargedAt;

	public Charge(Long memberId, int point) {
		this.memberId = memberId;
		this.point = point;
	}
}
