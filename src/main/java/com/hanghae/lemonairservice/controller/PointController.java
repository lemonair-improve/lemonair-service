package com.hanghae.lemonairservice.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.point.ChargePointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.DonatorRankingDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.security.PrincipalUtil;
import com.hanghae.lemonairservice.service.PointService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PointController {
	private final PointService pointService;

	@GetMapping("/points")
	public Mono<ResponseEntity<PointResponseDto>> getTotalPoint(Principal principal) {
		return pointService.getTotalPoint(PrincipalUtil.getMember(principal).getId());
	}

	@PostMapping("/points")
	public Mono<ResponseEntity<PointResponseDto>> chargePoint(@RequestBody ChargePointRequestDto chargePointRequestDto,
		Principal principal) {
		return pointService.chargePoints(chargePointRequestDto, PrincipalUtil.getMember(principal));
	}

	@PostMapping("/donations/{streamerId}")
	public Mono<ResponseEntity<PointResponseDto>> donate(@PathVariable Long streamerId,
		@RequestBody DonationRequestDto donationRequestDto, @AuthenticationPrincipal Principal user) {
		return pointService.donate(donationRequestDto, PrincipalUtil.getMember(user), streamerId);
	}

	@GetMapping("/donations/rank")
	public Mono<ResponseEntity<Page<DonatorRankingDto>>> donationRank(@AuthenticationPrincipal Principal principal,
		Pageable pageable) {
		return pointService.getAllDonators(pageable, PrincipalUtil.getMember(principal));
	}

}