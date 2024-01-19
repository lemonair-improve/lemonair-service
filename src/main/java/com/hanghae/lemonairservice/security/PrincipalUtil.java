package com.hanghae.lemonairservice.security;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.hanghae.lemonairservice.entity.Member;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PrincipalUtil {
	public static Member getMember(Principal principal) {
		log.info("principal:" + principal);
		if (principal instanceof Authentication authentication) {
			Object principalObject = authentication.getPrincipal();
			if (principalObject instanceof UserDetailsImpl userDetails) {
				return userDetails.getMember();
			}
		}
		throw new RuntimeException("존재하지 않는 사용자입니다.");
	}
}