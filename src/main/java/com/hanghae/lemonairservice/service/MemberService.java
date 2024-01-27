package com.hanghae.lemonairservice.service;

import static com.hanghae.lemonairservice.util.ThreadSchedulers.COMPUTE;
import static com.hanghae.lemonairservice.util.ThreadSchedulers.IO;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.lemonairservice.dto.member.LoginRequestDto;
import com.hanghae.lemonairservice.dto.member.LoginResponseDto;
import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.dto.member.SignUpResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.exception.ErrorCode;
import com.hanghae.lemonairservice.exception.ExpectedException;
import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;
import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final MemberChannelRepository memberChannelRepository;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public Mono<ResponseEntity<SignUpResponseDto>> signup(SignUpRequestDto signupRequestDto) {
		String streamKey = UUID.randomUUID().toString();
		return validateMemberExists(signupRequestDto).then(createMember(signupRequestDto, streamKey))
			.subscribeOn(IO.scheduler())
			.flatMap(this::createMemberChannel)
			.publishOn(COMPUTE.scheduler())
			.thenReturn(ResponseEntity.ok(new SignUpResponseDto(streamKey)));
	}

	public Mono<ResponseEntity<LoginResponseDto>> login(LoginRequestDto loginRequestDto) {
		return memberRepository.findByLoginId(loginRequestDto.getLoginId())
			.subscribeOn(IO.scheduler())
			.publishOn(COMPUTE.scheduler())
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

	private Mono<MemberChannel> createMemberChannel(Member member) {
		return memberChannelRepository.save(new MemberChannel(member))
			.onErrorResume(throwable -> Mono.error(new ExpectedException(ErrorCode.ChannelCreateFailed)));
	}

	private Mono<Member> createMember(SignUpRequestDto signupRequestDto, String streamKey) {
		return memberRepository.save(
				new Member(signupRequestDto, passwordEncoder.encode(signupRequestDto.getPassword()), streamKey))
			.onErrorResume(throwable -> Mono.error(new ExpectedException(ErrorCode.MemberCreateFailed)));
	}

}