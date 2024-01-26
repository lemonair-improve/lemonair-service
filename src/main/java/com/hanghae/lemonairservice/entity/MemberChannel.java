package com.hanghae.lemonairservice.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table("member_channel")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberChannel extends TimestampEntity {
	@Id
	private Long id;
	private String title;
	private Boolean onAir;

	@Column("total_streaming")
	private int totalStreaming;

	@Column("member_id")
	private Long memberId;

	@Setter
	@Transient
	private Member member;

	private LocalDateTime startedAt;

	public MemberChannel(Member member) {
		this.title = member.getNickname() + "의 방송";
		this.onAir = false;
		this.totalStreaming = 0;
		this.memberId = member.getId();
	}

	public void addTime(int time) {
		this.totalStreaming += time;
	}

}
