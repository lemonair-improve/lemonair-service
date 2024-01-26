package com.hanghae.lemonairservice.dto.member;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

	@Email(message = "올바른 이메일 형식이 아닙니다.")
	private String email;

	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$", message = "비밀번호는 영문 대소문자 숫자 및 특수문자를 포함한 8~20자리 문자여야합니다.")
	private String password;

	@NotBlank(message = "비밀번호 확인란을 입력해주세요")
	private String password2;

	@NotBlank(message = "아이디를 입력해주세요")
	private String loginId;

	@NotBlank(message = "닉네임을 입력해주세요")
	private String nickname;

	@AssertTrue(message = "비밀번호 확인이 일치하지 않습니다.")
	private boolean isPasswordRetypeValid() {
		return password.equals(password2);
	}
}