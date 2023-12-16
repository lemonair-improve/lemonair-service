package com.hanghae.lemonairservice.dto.channel;

import com.hanghae.lemonairservice.entity.MemberChannel;

import lombok.Getter;

@Getter
public class MemberChannelDetailResponseDto {
	private Long channelId;
	private String streamer;
	private String title;
	private String hlsUrl;

	public MemberChannelDetailResponseDto(MemberChannel memberChannel){
		this.channelId = memberChannel.getId();
		this.streamer = memberChannel.getStreamer();
		this.title = memberChannel.getTitle();
	}
	public void updateHlsUrl(String hlsUrl){
		this.hlsUrl= hlsUrl;
	}

}
