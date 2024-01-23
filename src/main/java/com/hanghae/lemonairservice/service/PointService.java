package com.hanghae.lemonairservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.lemonairservice.dto.point.ChargePointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationResponseDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Charge;
import com.hanghae.lemonairservice.entity.Donation;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.exception.point.DonationSaveException;
import com.hanghae.lemonairservice.exception.point.NotEnoughPointException;
import com.hanghae.lemonairservice.exception.point.PointChargeException;
import com.hanghae.lemonairservice.repository.ChargeRepository;
import com.hanghae.lemonairservice.repository.DonationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {
	private final ChargeRepository chargeRepository;
	private final DonationRepository donationRepository;

	public Mono<Integer> getTotalPoint(Long memberId) {
		return Mono.zip(chargeRepository.totalPoint(memberId), donationRepository.totalPoint(memberId))
			.flatMap(tuple -> Mono.just(tuple.getT1() + tuple.getT2()));
	}

	@Transactional
	public Mono<PointResponseDto> chargePoints(ChargePointRequestDto chargePointRequestDto, Member member) {
		return chargeRepository.save(new Charge(member.getId(), chargePointRequestDto.getPoint()))
			.onErrorResume(throwable -> Mono.error(PointChargeException::new))
			.then(getTotalPoint(member.getId()))
			.flatMap(totalPoint -> Mono.just(new PointResponseDto(member, totalPoint)));
	}

	@Transactional
	public Mono<DonationResponseDto> usePoint(DonationRequestDto donationRequestDto, Member member, Long streamerId) {
		return getTotalPoint(member.getId()).flatMap(remainPoint -> {
			if (remainPoint < donationRequestDto.getDonatePoint()) {
				return Mono.error(new NotEnoughPointException(remainPoint.toString()));
			}
			return donationRepository.save(new Donation(streamerId, member.getId(), donationRequestDto))
				.onErrorResume(throwable -> Mono.error(DonationSaveException::new))
				.flatMap(saveDonation -> Mono.just(
					new DonationResponseDto(remainPoint - donationRequestDto.getDonatePoint())));
		});
	}
}