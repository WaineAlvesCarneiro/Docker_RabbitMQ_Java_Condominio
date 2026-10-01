package com.condominiosaas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final UserDetailsService userDetailsService;
	private final SecretKey key;

	public JwtAuthenticationFilter(UserDetailsService userDetailsService, @Value("${app.jwt.key}") String secret) {
		this.userDetailsService = userDetailsService;
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Ignora validação de token no endpoint de login
		String path = request.getServletPath();
		if (path.equals("/Auth/login")) {
			filterChain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
				String username = claims.getSubject();
				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					var userDetails = userDetailsService.loadUserByUsername(username);
					var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					// guarda os claims para uso nos controllers
					auth.setDetails(claims);
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} catch (Exception ex) {
				// token inválido -> ignora e segue sem autenticação
			}
		}
		filterChain.doFilter(request, response);
	}
}
