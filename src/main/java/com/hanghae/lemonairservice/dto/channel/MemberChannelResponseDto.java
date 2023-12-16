package com.hanghae.lemonairservice.dto.channel;

import com.hanghae.lemonairservice.entity.MemberChannel;

import lombok.Getter;

@Getter
public class MemberChannelResponseDto {
	private Long channelId;
	private String streamer;
	private String title;
	private String thumbnailUrl;
	public MemberChannelResponseDto(MemberChannel memberChannel){
		this.channelId = memberChannel.getId();
		this.streamer = memberChannel.getStreamer();
		this.title = memberChannel.getTitle();
	}
	public void updateThumbnailUrl(String thumbnailUrl){
		this.thumbnailUrl = thumbnailUrl;
	}
}
