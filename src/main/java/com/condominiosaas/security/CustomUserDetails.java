package com.condominiosaas.security;

import com.condominiosaas.domain.entity.AuthUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {
	private final AuthUser user;

	public CustomUserDetails(AuthUser user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String role = user.getRole();
		if (role == null) return List.of();
		return List.of(new SimpleGrantedAuthority("ROLE_" + role));
	}

	@Override
	public String getPassword() {
		return user.getPasswordHash();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getAtivo() != null && user.getAtivo().name().equals("Ativo");
	}

	public UUID getId() { return user.getId(); }
	public Long getEmpresaId() { return user.getEmpresaId(); }
	public Boolean isPrimeiroAcesso() { return user.getPrimeiroAcesso(); }
}
