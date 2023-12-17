package com.hanghae.lemonairservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hanghae.lemonairservice.entity.MemberChannel;

import reactor.core.publisher.Flux;

public interface MemberChannelRepository extends ReactiveCrudRepository<MemberChannel, Long> {
	Flux<MemberChannel> findAllByOnAirIsTrue();
}
