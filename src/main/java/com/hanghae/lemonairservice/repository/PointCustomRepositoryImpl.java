package com.hanghae.lemonairservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;

import com.hanghae.lemonairservice.dto.point.DonatorRankingDto;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class PointCustomRepositoryImpl implements PointCustomRepository {
	private final DatabaseClient databaseClient;

	@Override
	public Flux<DonatorRankingDto> findSumOfPointByMemberId(Long memberId, Pageable pageable) {
		String sql = """
				SELECT 	p.donator_id as donatorId, 
						SUM(p.point) as sumOfPoint,
						m.nickname  as nickname
			FROM point p 
			LEFT JOIN member m ON p.donator_id = m.id
			WHERE p.member_id = :memberId AND 
			p.donator_id is not null 
			GROUP BY p.donator_id 
			ORDER BY sumOfPoint DESC
			""";
		return databaseClient.sql(sql)
			.bind("memberId", memberId)
			.fetch()
			.all()
			.map(row -> DonatorRankingDto.builder()
				.donatorId((Long)row.get("donatorId"))
				.sumOfPoint(Integer.parseInt(String.valueOf(row.get("sumOfPoint"))))
				.nickname((String)row.get("nickname"))
				.build());
	}
}
