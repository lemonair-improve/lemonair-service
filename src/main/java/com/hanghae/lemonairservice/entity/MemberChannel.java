package com.hanghae.lemonairservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table("member_channel")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberChannel {
	@Id
	private Long id;
	private String title;
	private String streamer;
	private Boolean onAir;

	public MemberChannel(String nickname){
		this.title = nickname + "의 방송";
		this.streamer = nickname;
		this.onAir = false;
	}
}
