package com.hanghae.lemonairservice.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.point.ChargePointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationResponseDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.security.PrincipalUtil;
import com.hanghae.lemonairservice.service.PointService;
import com.hanghae.lemonairservice.util.ResponseMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PointController {
	private final PointService pointService;

	@PostMapping("/points")
	public Mono<ResponseEntity<PointResponseDto>> addPoint(@RequestBody ChargePointRequestDto chargePointRequestDto,
		Principal principal) {
		return pointService.chargePoints(chargePointRequestDto, PrincipalUtil.getMember(principal))
			.flatMap(ResponseMapper::mapToResponse);
	}

	@PostMapping("/donations/{streamerId}")
	public Mono<ResponseEntity<DonationResponseDto>> usePoint(@PathVariable Long streamerId,
		@RequestBody DonationRequestDto donationRequestDto, @AuthenticationPrincipal Principal user) {
		return pointService.usePoint(donationRequestDto, PrincipalUtil.getMember(user), streamerId)
			.flatMap(ResponseMapper::mapToResponse);
	}

}
