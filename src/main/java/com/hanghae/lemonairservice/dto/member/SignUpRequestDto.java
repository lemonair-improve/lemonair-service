package com.hanghae.lemonairservice.dto.member;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

	@Email
	private String email;

	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$", message = "비밀번호는 영문 대소문자 숫자 및 특수문자를 포함한 8~20자리 문자여야합니다.")
	private String password;

	@NotBlank
	private String password2;

	@NotBlank
	private String loginId;

	@NotBlank
	private String nickname;

	@AssertTrue(message = "비밀번호 확인이 일치하지 않습니다.")
	private boolean isPasswordRetypeValid() {
		return password.equals(password2);
	}
}

