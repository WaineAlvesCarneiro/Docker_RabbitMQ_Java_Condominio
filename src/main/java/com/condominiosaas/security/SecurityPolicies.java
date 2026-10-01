package com.condominiosaas.security;

import io.jsonwebtoken.Claims;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("securityPolicies")
public class SecurityPolicies {

	public boolean isAdminPolicy(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) return false;

		// roles permitidas: Suporte, Sindico, Porteiro
		boolean hasRole = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(r -> r.equals("ROLE_Suporte")
					|| r.equals("ROLE_Sindico") || r.equals("ROLE_Porteiro"));
		if (!hasRole) return false;

		Object details = authentication.getDetails();

		String primeiroAcesso = null;
		String statusAtivo = null;
		String empresaAtiva = null;

		if (details instanceof Claims) {
			Claims claims = (Claims) details;
			primeiroAcesso = claims.get("primeiroAcesso", String.class);
			statusAtivo = claims.get("statusAtivo", String.class);
			empresaAtiva = claims.get("empresaAtiva", String.class);
		} else if (details instanceof Map<?, ?> map) {
			Object valor = map.get("primeiroAcesso");
			primeiroAcesso = valor != null ? valor.toString() : "false";
			Object status = map.get("statusAtivo");
			statusAtivo = status != null ? status.toString() : "Inativo";
			Object empresa = map.get("empresaAtiva");
			empresaAtiva = empresa != null ? empresa.toString() : "Inativo";
		}

		return "false".equals(primeiroAcesso) && "Ativo".equals(statusAtivo) && "Ativo".equals(empresaAtiva);
	}

	public boolean permitirTrocaSenha(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) return false;

		boolean hasRole = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.anyMatch(r -> r.equals("ROLE_Suporte") || r.equals("ROLE_Sindico") || r.equals("ROLE_Porteiro"));
		if (!hasRole) return false;

		Object details = authentication.getDetails();
		String primeiroAcesso = null;
		String statusAtivo = null;
		String empresaAtiva = null;

		if (details instanceof Claims) {
			Claims claims = (Claims) details;
			primeiroAcesso = claims.get("primeiroAcesso", String.class);
			statusAtivo = claims.get("statusAtivo", String.class);
			empresaAtiva = claims.get("empresaAtiva", String.class);
		} else if (details instanceof Map<?, ?> map) {
			Object valor = map.get("primeiroAcesso");
			primeiroAcesso = valor != null ? valor.toString() : "false";
			Object status = map.get("statusAtivo");
			statusAtivo = status != null ? status.toString() : "Inativo";
			Object empresa = map.get("empresaAtiva");
			empresaAtiva = empresa != null ? empresa.toString() : "Inativo";
		}

		return "true".equals(primeiroAcesso) && "Ativo".equals(statusAtivo) && "Ativo".equals(empresaAtiva);
	}
}
