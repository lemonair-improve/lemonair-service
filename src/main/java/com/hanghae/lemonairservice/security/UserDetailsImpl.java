package com.hanghae.lemonairservice.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hanghae.lemonairservice.entity.Member;

import lombok.Getter;

public class UserDetailsImpl implements UserDetails {

	@Getter
	private final Member member;
	private final String loginId;

	public UserDetailsImpl(Member member, String loginId) {
		this.member = member;
		this.loginId = loginId;
	}

	public Member getMember() {
		return member;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	public String getLoginId() {
		return this.loginId;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}