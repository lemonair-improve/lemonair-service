package com.hanghae.lemonairservice.service;

import static com.hanghae.lemonairservice.util.ThreadSchedulers.*;

import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.exception.ErrorCode;
import com.hanghae.lemonairservice.exception.ExpectedException;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService {
	private final MemberChannelRepository memberChannelRepository;
	private final MemberRepository memberRepository;

	public Mono<Boolean> checkStreamValidity(String streamerId, StreamKeyRequestDto streamKey) {
		return findMemberByLoginId(streamerId)
			.subscribeOn(IO.scheduler())
			.publishOn(COMPUTE.scheduler())
			.filter(member -> member.getStreamKey().equals(streamKey.getStreamKey()))
			.switchIfEmpty(Mono.defer(() -> Mono.error(new ExpectedException(ErrorCode.StreamKeyMismatch))))
			.thenReturn(true);
	}

	public Mono<Boolean> startStream(String streamerId) {
		return findMemberByLoginId(streamerId).flatMap(this::findMemberChannelByMember)
			.subscribeOn(IO.scheduler())
			.filter(memberChannel -> !memberChannel.getOnAir())
			.switchIfEmpty(Mono.defer(() -> Mono.error(new ExpectedException(ErrorCode.BroadcastAlreadyStarted))))
			.doOnNext(MemberChannel::onAir)
			.flatMap(this::saveMemberChannel)
			.publishOn(COMPUTE.scheduler())
			.thenReturn(true);
	}

	public Mono<Boolean> stopStream(String streamerId) {
		return findMemberByLoginId(streamerId).flatMap(this::findMemberChannelByMember)
			.subscribeOn(IO.scheduler())
			.filter(MemberChannel::getOnAir)
			.switchIfEmpty(Mono.defer(() -> Mono.error(new ExpectedException(ErrorCode.BroadcastNotStartedYet))))
			.doOnNext(MemberChannel::offAir)
			.flatMap(this::saveMemberChannel)
			.publishOn(COMPUTE.scheduler())
			.thenReturn(true);
	}

	private Mono<Member> findMemberByLoginId(String memberLoginId) {
		return memberRepository.findByLoginId(memberLoginId)
			.switchIfEmpty(Mono.defer(() -> Mono.error(new ExpectedException(ErrorCode.MemberNotFound))));
	}

	private Mono<MemberChannel> findMemberChannelByMember(Member member) {
		return memberChannelRepository.findByMemberId(member.getId())
			.switchIfEmpty(Mono.defer(() -> Mono.error(new ExpectedException(ErrorCode.ChannelNotFound))));
	}

	private Mono<MemberChannel> saveMemberChannel(MemberChannel memberChannel) {
		return memberChannelRepository.save(memberChannel)
			.onErrorResume(throwable -> Mono.error(new ExpectedException(ErrorCode.ChannelSaveFailed)));
	}

}
