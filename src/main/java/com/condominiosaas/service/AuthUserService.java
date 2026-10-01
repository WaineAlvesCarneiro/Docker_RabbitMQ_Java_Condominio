package com.condominiosaas.service;

import com.condominiosaas.domain.entity.AuthUser;
import com.condominiosaas.dto.AuthUserDto;
import com.condominiosaas.domain.enums.TipoRole;
import com.condominiosaas.dto.CreateAuthUserRequest;
import com.condominiosaas.dto.UpdateAuthUserRequest;
import com.condominiosaas.repository.AuthUserRepository;
import com.condominiosaas.messaging.RabbitProducer;
import com.condominiosaas.messaging.EnvioEmailRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Random;
import static java.time.LocalDateTime.now;

@Service
public class AuthUserService {
	private final AuthUserRepository authUserRepository;
	private final PasswordEncoder encoder;
	private final RabbitProducer producer;
	private final EmailTemplateService emailTemplateService;

	public AuthUserService(AuthUserRepository authUserRepository, PasswordEncoder encoder, RabbitProducer producer, 
		EmailTemplateService emailTemplateService) {
			this.authUserRepository = authUserRepository;
			this.encoder = encoder;
			this.producer = producer;
			this.emailTemplateService = emailTemplateService;
	}

	public Optional<AuthUserDto> getById(UUID id) {
		return authUserRepository.findById(id).map(this::toDto);
	}

	public List<AuthUserDto> getAll(Long empresaId) {
		List<AuthUser> users;
		if (empresaId == null || empresaId == 0) users = authUserRepository.findAll();
		else users = authUserRepository.findAll().stream().filter(u -> u.getEmpresaId() == null || u.getEmpresaId().equals(empresaId)).toList();

		return users.stream().map(this::toDto).toList();
	}

	public Page<AuthUserDto> getPaged(int page, int pageSize, String sortBy, String direction, Long empresaId, String userName) {
		var pageUsers = authUserRepository.getAllPaged(page, pageSize, sortBy, direction, empresaId, userName);
		return mapPage(pageUsers);
	}

	private Page<AuthUserDto> mapPage(Page<AuthUser> pageUsers) {
		return pageUsers.map(this::toDto);
	}

	public AuthUser create(CreateAuthUserRequest req) {
		AuthUser u = new AuthUser();
		u.setId(UUID.randomUUID());
		u.setUserName(req.getUserName());
		u.setEmail(req.getEmail());
		u.setRole(req.getRole());
		u.setEmpresaId(req.getEmpresaId());
		u.setDataInclusao(now());
		u.setPrimeiroAcesso(true);

		String senhaTemporaria = req.getSenha();
		if (senhaTemporaria != null && !senhaTemporaria.isEmpty()) {
			u.setPasswordHash(encoder.encode(senhaTemporaria));
			u.setPrimeiroAcesso(false);
		} else {
			senhaTemporaria = gerarSenhaAleatoria(5);
			u.setPasswordHash(encoder.encode(senhaTemporaria));
			u.setPrimeiroAcesso(true);
		}

		authUserRepository.save(u);

		// enviar email de boas-vindas com senha temporária
		try {
			enviarBoasVindasEmail(u, senhaTemporaria);
		} catch (Exception ex) {
			// não interrompe fluxo
		}

		return u;
	}

	private void enviarBoasVindasEmail(AuthUser u, String senhaTemporaria) {
		var corpo = emailTemplateService.gerarBoasVindasUsuario(u.getUserName(), senhaTemporaria);
		var emailRequest = new EnvioEmailRequest(
				u.getEmail(),
				"Bem-vindo ao Sistema",
				corpo,
				u.getEmpresaId() == null ? 0L : u.getEmpresaId()
		);
		producer.publicarMensagem(emailRequest);
	}

	private String gerarSenhaAleatoria(int tamanho) {
		final String caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
		var random = new Random();
		var sb = new StringBuilder(tamanho);
		for (int i = 0; i < tamanho; i++) sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
		return sb.toString();
	}

	public boolean update(UUID id, UpdateAuthUserRequest req) {
		return authUserRepository.findById(id).map(u -> {
			u.setUserName(req.getUserName());
			u.setEmail(req.getEmail());
			if (req.getRole() != null) u.setRole(req.getRole());
			if (req.getEmpresaId() != null) u.setEmpresaId(req.getEmpresaId());
			if (req.getSenha() != null && !req.getSenha().isEmpty()) {
				u.setPasswordHash(encoder.encode(req.getSenha()));
				u.setPrimeiroAcesso(false);
			}
			u.setDataAlteracao(now());
			authUserRepository.save(u);
			// enviar email de alteração cadastral
			try {
				String corpo = emailTemplateService.gerarUsuarioAlterado(u.getUserName());
				EnvioEmailRequest emailRequest = new EnvioEmailRequest(
						u.getEmail(),
						"Usuário alteração de Dados Cadastrais",
						corpo,
						u.getEmpresaId() == null ? 0L : u.getEmpresaId()
				);
				producer.publicarMensagem(emailRequest);
			} catch (Exception ex) {
				// não interrompe fluxo
			}
			return true;
		}).orElse(false);
	}

	public boolean delete(UUID id) {
		return authUserRepository.findById(id).map(u -> {
			authUserRepository.delete(u);
			return true;
		}).orElse(false);
	}

	private AuthUserDto toDto(AuthUser u) {
		AuthUserDto d = new AuthUserDto();
		d.setId(u.getId());
		d.setAtivo(u.getAtivo());
		d.setEmpresaAtiva(u.getEmpresaAtiva());
		d.setEmpresaId(u.getEmpresaId());
		d.setUserName(u.getUserName());
		d.setEmail(u.getEmail());
		d.setPrimeiroAcesso(u.getPrimeiroAcesso() != null ? u.getPrimeiroAcesso() : false);
		try { d.setRole(TipoRole.valueOf(u.getRole())); } catch (Exception ex) { d.setRole(null); }
		return d;
	}
}
