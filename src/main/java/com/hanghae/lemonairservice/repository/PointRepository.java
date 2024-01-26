package com.hanghae.lemonairservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;

import com.hanghae.lemonairservice.entity.Point;

import reactor.core.publisher.Mono;

public interface PointRepository extends ReactiveCrudRepository<Point, Long> {
	@Query(value = "select sum( case when p.donator_id = :memberId then -point when p.member_id = :memberId then point else 0 end ) from point p")
	Mono<Integer> totalPoint(Long memberId);
}
