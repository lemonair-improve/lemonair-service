package com.hanghae.lemonairservice.service;

import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.member.LoginRequestDto;
import com.hanghae.lemonairservice.dto.member.LoginResponseDto;
import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.dto.member.SignUpResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.exception.ErrorCode;
import com.hanghae.lemonairservice.exception.ExpectedException;
import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;
import com.hanghae.lemonairservice.repository.PointRepository;
import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final MemberChannelRepository memberChannelRepository;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PointRepository pointRepository;

	@Transactional
	public Mono<ResponseEntity<SignUpResponseDto>> signup(SignUpRequestDto signupRequestDto) {
		String streamKey = UUID.randomUUID().toString();
		return validateMemberExists(signupRequestDto)
			.then(createMember(signupRequestDto))
			.flatMap(saveMember -> new SignUpResponseDto(saveMember.getStreamKey()))
			.flatMap(this::createMemberChannelAndPoint)
			.thenReturn(ResponseEntity.ok(new SignUpResponseDto(streamKey)));
	}

	public Mono<ResponseEntity<LoginResponseDto>> login(LoginRequestDto loginRequestDto) {
		return memberRepository.findByLoginId(loginRequestDto.getLoginId())
			.switchIfEmpty(Mono.defer(() -> Mono.error(new ExpectedException(ErrorCode.MemberNotFound))))
			.flatMap(member -> passwordMatches(loginRequestDto, member.getPassword()).then(
					Mono.zip(jwtUtil.createAccessToken(member.getLoginId(), member.getNickname()),
						jwtUtil.createRefreshToken(member.getLoginId(), member.getNickname())))
				.flatMap(tuple -> refreshTokenRepository.saveRefreshToken(member.getLoginId(), tuple.getT2())
					.thenReturn(ResponseEntity.ok().body(new LoginResponseDto(tuple.getT1(), tuple.getT2())))));
	}

	public Mono<ResponseEntity<String>> logout(String loginId) {
		return refreshTokenRepository.deleteByLoginId(loginId)
			.flatMap(logout -> Mono.just(ResponseEntity.ok("로그아웃되었습니다.")));
	}

	private Mono<Boolean> passwordMatches(LoginRequestDto loginRequestDto, String password) {
		return Mono.just(passwordEncoder.matches(loginRequestDto.getPassword(), password));
	}

	private Mono<Void> validateExists(Mono<Boolean> mono, ErrorCode errorCode) {
		return mono.flatMap(exists -> exists ? Mono.error(new ExpectedException(errorCode)) : Mono.empty()).then();
	}

	private Mono<Void> validateMemberExists(SignUpRequestDto signupRequestDto) {
		Mono<Boolean> emailExists = memberRepository.existsByEmail(signupRequestDto.getEmail());
		Mono<Boolean> nicknameExists = memberRepository.existsByNickname(signupRequestDto.getNickname());
		Mono<Boolean> loginIdExists = memberRepository.existsByLoginId(signupRequestDto.getLoginId());
		return Mono.when(validateExists(emailExists, ErrorCode.EmailAlreadyExists),
			validateExists(nicknameExists, ErrorCode.NicknameAlreadyExists),
			validateExists(loginIdExists, ErrorCode.LoginIdAlreadyExists)).then();
	}

	private Mono<Void> createMemberChannelAndPoint(Member member) {
		Mono<MemberChannel> saveChannelMono = memberChannelRepository.save(new MemberChannel(member))
			.onErrorResume(throwable -> Mono.error(new ExpectedException(ErrorCode.ChannelCreateFailed)));
		Mono<Point> savePointMono = pointRepository.save(new Point(member))
			.onErrorResume(throwable -> Mono.error(new ExpectedException(ErrorCode.PointCreateFailed)));
		return Mono.zip(saveChannelMono, savePointMono).then();
	}

	private Mono<Member> createMember(SignUpRequestDto signupRequestDto) {
		return memberRepository.save(
				new Member(signupRequestDto, passwordEncoder.encode(signupRequestDto.getPassword())))
			.onErrorResume(throwable -> Mono.error(new ExpectedException(ErrorCode.MemberCreateFailed)));
	}

}


