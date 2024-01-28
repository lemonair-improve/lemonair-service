package com.hanghae.lemonairservice.service;

import static com.hanghae.lemonairservice.util.ThreadSchedulers.COMPUTE;
import static com.hanghae.lemonairservice.util.ThreadSchedulers.IO;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
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
	private LocalDateTime endedAt;
	private LocalDateTime startedAt;

	public Mono<Boolean> checkStreamValidity(String streamerId, StreamKeyRequestDto streamKey) {
		log.info(streamKey.getStreamKey());
		return memberRepository.findByLoginId(streamerId)
			.subscribeOn(IO.scheduler())
			.publishOn(COMPUTE.scheduler())
			.filter(member -> member.getStreamKey().equals(streamKey.getStreamKey()))
			.switchIfEmpty(Mono.error(new RuntimeException("스트림 키가 일치하지 않습니다.")))
			.thenReturn(true);
	}

	public Mono<Boolean> startStream(String streamerId) {
		return memberRepository.findByLoginId(streamerId)
			.subscribeOn(IO.scheduler())
			.switchIfEmpty(Mono.error(new RuntimeException("방송시작요청 멤버조회실패" + streamerId + " 는 가입되지 않은 아이디입니다.")))
			.flatMap(member -> memberChannelRepository.findByMemberId(member.getId())
				.switchIfEmpty(Mono.error(new RuntimeException("해당 멤버의 채널이 존재하지 않습니다.")))
				.flatMap(memberChannel -> {
					memberChannel.setOnAir(true);
					memberChannel.setStartedAt(LocalDateTime.now());
					return memberChannelRepository.save(memberChannel)
						.publishOn(COMPUTE.scheduler())
						.thenReturn(true);
				}));
	}

	public Mono<Boolean> stopStream(String streamerId) {
		return memberRepository.findByLoginId(streamerId)
			.subscribeOn(IO.scheduler())
			.switchIfEmpty(Mono.error(new RuntimeException("방송종료 요청 멤버조회실패" + streamerId + " 는 가입되지 않은 아이디입니다.")))
			.flatMap(member -> memberChannelRepository.findByMemberId(member.getId())
				.switchIfEmpty(Mono.error(new RuntimeException("해당 멤버의 채널이 존재하지 않습니다.")))
				.flatMap(memberChannel -> {
					memberChannel.setOnAir(false);
					startedAt = memberChannel.getStartedAt();
					if (startedAt == null) {
						return Mono.error(new RuntimeException("시작되지 않은 방송입니다."));
					}
					endedAt = LocalDateTime.now();
					Duration duration = Duration.between(startedAt, endedAt);
					long minutesDifference = duration.toMinutes();
					memberChannel.setStartedAt(null);
					memberChannel.addTime((int)minutesDifference);
					return memberChannelRepository.save(memberChannel)
						.publishOn(COMPUTE.scheduler())
						.thenReturn(true);
				}));
	}
}
