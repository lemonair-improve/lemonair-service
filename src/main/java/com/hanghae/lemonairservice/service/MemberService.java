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

	private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
	private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final MemberChannelRepository memberChannelRepository;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PointRepository pointRepository;


	private static boolean validatePassword(String password) {
		return pattern.matcher(password).matches();
	}

	@Transactional
	public Mono<ResponseEntity<SignUpResponseDto>> signup(SignUpRequestDto signupRequestDto) {
		String streamKey = UUID.randomUUID().toString();

		return validatePassword(signupRequestDto)
			.then(validateMemberExists(signupRequestDto))
			.then(createMember(signupRequestDto, streamKey))
			.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입에 실패했습니다.")))
			.flatMap(this::createMemberChannelAndPoint)
			.thenReturn(ResponseEntity.ok(new SignUpResponseDto(streamKey)));
		}



	public Mono<ResponseEntity<LoginResponseDto>> login(LoginRequestDto loginRequestDto) {
		return memberRepository.findByLoginId(loginRequestDto.getLoginId())
			.switchIfEmpty(Mono.error(new RuntimeException("아이디가 잘못되었습니다.")))
			.flatMap(member -> passwordMatches(loginRequestDto, member.getPassword())
				.then(Mono.zip(
					jwtUtil.createAccessToken(member.getLoginId(), member.getNickname()),
					jwtUtil.createRefreshToken(member.getLoginId(), member.getNickname())
				))
				.flatMap(tuple -> refreshTokenRepository.saveRefreshToken(member.getLoginId(), tuple.getT2())
					.thenReturn(ResponseEntity.ok().body(new LoginResponseDto(tuple.getT1(), tuple.getT2()))))
			);
	}



	public Mono<ResponseEntity<String>> logout(String loginId) {
		return refreshTokenRepository.deleteByLoginId(loginId)
			.flatMap(logout -> Mono.just(ResponseEntity.ok("로그아웃되었습니다.")));
	}



	private Mono<Boolean> passwordMatches(LoginRequestDto loginRequestDto, String password) {
		return Mono.just(passwordEncoder.matches(loginRequestDto.getPassword(), password));
	}


	private Mono<Void> validatePassword(SignUpRequestDto signupRequestDto) {
		if (!validatePassword(signupRequestDto.getPassword())) {
			return Mono.error(
				new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호는 최소 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다."));
		}

		if (!signupRequestDto.getPassword().equals(signupRequestDto.getPassword2())) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."));
		}

		return Mono.empty();
	}

	private Mono<Void> validateExists(Mono<Boolean> mono, String errorMessage) {
		return mono
			.flatMap(exists -> exists ? Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage)) : Mono.empty())
			.then();
	}

	private Mono<Void> validateMemberExists(SignUpRequestDto signupRequestDto) {
		Mono<Boolean> emailExists = memberRepository.existsByEmail(signupRequestDto.getEmail());
		Mono<Boolean> nicknameExists = memberRepository.existsByNickname(signupRequestDto.getNickname());
		Mono<Boolean> loginIdExists = memberRepository.existsByLoginId(signupRequestDto.getLoginId());

		return Mono.when(
			validateExists(emailExists, "해당 이메일은 이미 사용 중입니다."),
			validateExists(nicknameExists, "해당 닉네임은 이미 사용 중입니다."),
			validateExists(loginIdExists, "해당 아이디는 이미 사용 중입니다.")
		).then();
	}

	private Mono<Void> createMemberChannelAndPoint(Member member) {
		Mono<MemberChannel> saveChannelMono = memberChannelRepository.save(new MemberChannel(member))
			.onErrorResume(throwable -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "채널 생성에 실패했습니다.")));

		Mono<Point> savePointMono = pointRepository.save(new Point(member))
			.onErrorResume(throwable -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "포인트 생성에 실패했습니다.")));

		return Mono.zip(saveChannelMono, savePointMono).then();  // Convert to Mono<Void>
	}

	private Mono<Member> createMember(SignUpRequestDto signupRequestDto, String streamKey) {
		return memberRepository.save(
			new Member(signupRequestDto.getEmail(),
				passwordEncoder.encode(signupRequestDto.getPassword()),
				signupRequestDto.getLoginId(), signupRequestDto.getNickname(), streamKey));
	}

}


