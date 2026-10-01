package com.condominiosaas.service;

import com.condominiosaas.domain.entity.AuthUser;
import com.condominiosaas.repository.AuthUserRepository;
import com.condominiosaas.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
	private final AuthUserRepository repo;
	private final PasswordEncoder encoder;
	private final JwtUtil jwtUtil;

	public AuthService(AuthUserRepository repo, PasswordEncoder encoder, JwtUtil jwtUtil) {
		this.repo = repo;
		this.encoder = encoder;
		this.jwtUtil = jwtUtil;
	}

	public Optional<Map<String, Object>> authenticate(String username, String password) {
		var userOpt = repo.findByUserName(username);
		if (userOpt.isEmpty()) return Optional.empty();
		AuthUser user = userOpt.get();
		if (!encoder.matches(password, user.getPasswordHash())) return Optional.empty();

		Map<String, Object> claims = new HashMap<>();
		// claim names must match AuthClaims constants from .NET
		claims.put("role", user.getRole());
		claims.put("empresaId", user.getEmpresaId() != null ? String.valueOf(user.getEmpresaId()) : null);
		// primeiroAcesso expected as string "true"/"false"
		claims.put("primeiroAcesso", user.getPrimeiroAcesso() != null && user.getPrimeiroAcesso() ? "true" : "false");
		// statusAtivo and empresaAtiva expected as string names like "Ativo"/"Inativo"
		claims.put("statusAtivo", user.getAtivo() != null ? user.getAtivo().name() : null);
		claims.put("empresaAtiva", user.getEmpresaAtiva() != null ? user.getEmpresaAtiva().name() : null);

		String token = jwtUtil.generateToken(user.getUserName(), claims);
		Map<String, Object> result = new HashMap<>();
		result.put("token", token);
		result.put("primeiroAcesso", user.getPrimeiroAcesso());
		return Optional.of(result);
	}

	public boolean definirSenhaPermanente(String username, String novaSenha) {
		var userOpt = repo.findByUserName(username);
		if (userOpt.isEmpty()) return false;
		AuthUser user = userOpt.get();
		if (user.getPrimeiroAcesso() == null || !user.getPrimeiroAcesso()) return false;

		user.setPasswordHash(encoder.encode(novaSenha));
		user.setPrimeiroAcesso(false);
		user.setDataAlteracao(java.time.LocalDateTime.now());
		repo.save(user);
		return true;
	}
}
