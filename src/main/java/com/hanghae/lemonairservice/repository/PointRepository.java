package com.hanghae.lemonairservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.hanghae.lemonairservice.entity.Point;

import reactor.core.publisher.Mono;

public interface PointRepository extends R2dbcRepository<Point, Long>, PointCustomRepository {
	@Query(value = "select sum( case when p.donator_id = :memberId then -point when p.member_id = :memberId then point else 0 end ) from point p")
	Mono<Integer> totalPoint(Long memberId);
}
