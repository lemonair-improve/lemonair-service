package com.hanghae.lemonairservice.service;

import static com.hanghae.lemonairservice.util.ThreadSchedulers.COMPUTE;
import static com.hanghae.lemonairservice.util.ThreadSchedulers.IO;

import java.time.Duration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.lemonairservice.dto.point.ChargePointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.exception.ErrorCode;
import com.hanghae.lemonairservice.exception.ExpectedException;
import com.hanghae.lemonairservice.repository.PointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	private final PointRepository pointRepository;

	public Mono<ResponseEntity<PointResponseDto>> getTotalPoint(Long memberId) {
		return queryTotalPoint(memberId)
			.subscribeOn(IO.scheduler())
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
		return reactiveRedisTemplate.opsForValue().get(String.valueOf(member.getId()))
			.subscribeOn(IO.scheduler())
			.flatMap(existingValue -> Mono.error(new ExpectedException(ErrorCode.DuplicateRequest)))
			.switchIfEmpty(reactiveRedisTemplate.opsForValue().set(String.valueOf(member.getId()), "ing", Duration.ofSeconds(3)))
			.then(queryTotalPoint(member.getId())
				.filter(totalPoint -> totalPoint >= donationRequestDto.getDonatePoint())
				.switchIfEmpty(Mono.defer(() -> Mono.error(new ExpectedException(ErrorCode.NotEnoughPoint))))
				.flatMap(totalPoint -> pointRepository.save(new Point(donationRequestDto, streamerId, member.getId()))
					.publishOn(COMPUTE.scheduler())
					.onErrorResume(throwable -> Mono.error(RuntimeException::new))
					.flatMap(savePoint -> Mono.just(ResponseEntity.ok(new PointResponseDto(totalPoint - savePoint.getPoint()))))
				)
			);
	}

	private Mono<Integer> queryTotalPoint(Long memberId) {
		return pointRepository.totalPoint(memberId)
			.onErrorResume(throwable -> Mono.error(new ExpectedException(ErrorCode.PointQueryFailed)));
	}

	// public Mono<ResponseEntity<Flux<DonationRankingDto>>> donationRank(Member member) {
	// 	Flux<DonationRankingDto> donationRankDto = pointRepository.findByStreamerIdOrderBySumOfDonateLimit10(
	// 			member.getId())
	// 		.concatMap(userId -> pointRepository.findById(userId).flux())
	// 		.map(point -> new DonationRankingDto(point.getNickname()));
	//
	// 	return Mono.just(ResponseEntity.ok(donationRankDto));
	// }
}