package com.hanghae.lemonairservice.repository;

import org.springframework.data.domain.Pageable;

import com.hanghae.lemonairservice.dto.point.DonatorRankingDto;

import reactor.core.publisher.Flux;

public interface PointCustomRepository {
	Flux<DonatorRankingDto> findSumOfPointByMemberId(Long memberId, Pageable pageable);
}
