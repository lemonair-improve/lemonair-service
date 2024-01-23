package com.hanghae.lemonairservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.hanghae.lemonairservice.entity.Charge;

import reactor.core.publisher.Mono;

public interface ChargeRepository extends R2dbcRepository<Charge, Long> {

	@Query(value = "select sum(point) from charge c where member_id = :memberId;")
	Mono<Integer> totalPoint(Long memberId);
}
