package com.hanghae.lemonairservice.service;

import static com.hanghae.lemonairservice.util.ThreadSchedulers.*;

import com.hanghae.lemonairservice.dto.point.DonationWebClientDto;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.lemonairservice.dto.point.ChargePointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.DonatorRankingDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.exception.ErrorCode;
import com.hanghae.lemonairservice.exception.ExpectedException;
import com.hanghae.lemonairservice.repository.PointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	private final PointRepository pointRepository;

	@Value("${chat.url}")
	private String chatUrl;

	@Autowired
	private WebClient webClient;

	public Mono<ResponseEntity<PointResponseDto>> getTotalPoint(Long memberId) {
		return queryTotalPoint(memberId).subscribeOn(IO.scheduler())
			.publishOn(COMPUTE.scheduler())
			.map(point -> ResponseEntity.ok(new PointResponseDto(point)));
	}

	@Transactional
	public Mono<ResponseEntity<PointResponseDto>> chargePoints(ChargePointRequestDto chargePointRequestDto,
		Member member) {
		return pointRepository.save(new Point(chargePointRequestDto, member.getId()))
			.subscribeOn(IO.scheduler())
			.onErrorResume(throwable -> Mono.error(new ExpectedException(ErrorCode.PointChargeFailed)))
			.then(queryTotalPoint(member.getId()))
			.publishOn(COMPUTE.scheduler())
			.flatMap(totalPoint -> Mono.just(ResponseEntity.ok(new PointResponseDto(totalPoint))));
	}

	@Transactional
	public Mono<ResponseEntity<PointResponseDto>> donate(DonationRequestDto donationRequestDto, Member member, Long streamerId) {
		String memberId = String.valueOf(member.getId());

		return reactiveRedisTemplate.opsForValue().get(memberId)
			.subscribeOn(IO.scheduler())
			.flatMap(existingValue -> Mono.error(new ExpectedException(ErrorCode.DuplicateRequest)))
			.switchIfEmpty(reactiveRedisTemplate.opsForValue().set(memberId, "ing", Duration.ofSeconds(3)))
			.then(queryAndSavePoint(donationRequestDto, member, streamerId))
			.flatMap(savedPoint -> sendDonationWebClientRequest(donationRequestDto, member, streamerId, savedPoint));
	}

	public Mono<ResponseEntity<Page<DonatorRankingDto>>> getAllDonators(Pageable pageable, Member member) {
		return pointRepository.findSumOfPointByMemberId(member.getId(), pageable)
			.subscribeOn(IO.scheduler())
			.publishOn(COMPUTE.scheduler())
			.collectList()
			.map(p -> ResponseEntity.ok(new PageImpl<>(p, pageable, p.size())));
	}

	private Mono<Integer> queryTotalPoint(Long memberId) {
		return pointRepository.totalPoint(memberId)
			.onErrorResume(throwable -> Mono.error(new ExpectedException(ErrorCode.PointQueryFailed)));
	}

	private Mono<Integer> queryAndSavePoint(DonationRequestDto donationRequestDto, Member member, Long streamerId) {
		return queryTotalPoint(member.getId())
			.filter(totalPoint -> totalPoint >= donationRequestDto.getDonatePoint())
			.switchIfEmpty(Mono.defer(() -> Mono.error(new ExpectedException(ErrorCode.NotEnoughPoint))))
			.flatMap(totalPoint ->
				pointRepository.save(new Point(donationRequestDto, streamerId, member.getId()))
					.map(Point::getPoint)
			);
	}

	private Mono<ResponseEntity<PointResponseDto>> sendDonationWebClientRequest(DonationRequestDto donationRequestDto, Member member, Long streamerId, int savedPoint) {
		return webClient.post()
			.uri(chatUrl + "{streamerId}", streamerId.toString())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.bodyValue(new DonationWebClientDto(member.getNickname(), streamerId,
				donationRequestDto.getContents(), donationRequestDto.getDonatePoint()))
			.retrieve()
			.bodyToMono(Void.class)
			.onErrorResume(throwable -> Mono.error(new RuntimeException()))
			.then(Mono.just(ResponseEntity.ok(new PointResponseDto(savedPoint))));
	}
}