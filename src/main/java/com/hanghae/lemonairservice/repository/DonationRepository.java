package com.hanghae.lemonairservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hanghae.lemonairservice.entity.Donation;

import reactor.core.publisher.Mono;

public interface DonationRepository extends ReactiveCrudRepository<Donation, Long> {
	@Query(value = "select sum( case when d.donator_id = :memberId then -donate_point when d.streamer_id = :memberId then donate_point else 0 end ) from donation d")
	Mono<Integer> totalPoint(Long memberId);
}
