package com.condominiosaas.security;

import com.condominiosaas.domain.entity.AuthUser;
import com.condominiosaas.repository.AuthUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final AuthUserRepository repo;

	public CustomUserDetailsService(AuthUserRepository repo) {
		this.repo = repo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AuthUser user = repo.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
		return new CustomUserDetails(user);
	}
}
