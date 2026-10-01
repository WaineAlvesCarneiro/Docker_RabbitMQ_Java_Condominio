package com.condominiosaas.config;

import com.condominiosaas.domain.entity.AuthUser;
import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.domain.enums.TipoUserAtivo;
import com.condominiosaas.repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataLoader {

	private final AuthUserRepository repo;
	private final PasswordEncoder encoder;

	@Value("${app.admin.userName:Admin}")
	private String adminUserName;

	@Value("${app.admin.email:emailadmin@gmail.com}")
	private String adminEmail;

	@Value("${app.admin.password:12345}")
	private String adminPassword;

	public DataLoader(AuthUserRepository repo, PasswordEncoder encoder) {
		this.repo = repo;
		this.encoder = encoder;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void seedAdmin() {
		try {
			var opt = repo.findByUserName(adminUserName);
			if (opt.isPresent()) return;

			AuthUser u = new AuthUser();
			u.setId(UUID.randomUUID());
			u.setUserName(adminUserName);
			u.setEmail(adminEmail);
			u.setPasswordHash(encoder.encode(adminPassword));
			u.setRole("Suporte");
			// Forçar troca de senha no primeiro acesso
			u.setPrimeiroAcesso(true);
			u.setAtivo(TipoUserAtivo.Ativo);
			u.setEmpresaAtiva(TipoEmpresaAtivo.Ativo);
			u.setDataInclusao(LocalDateTime.now());
			u.setEmpresaId(null);

			repo.save(u);
		} catch (Exception ex) {
			// log if needed
			System.err.println("Erro ao seedar admin: " + ex.getMessage());
		}
	}
}
