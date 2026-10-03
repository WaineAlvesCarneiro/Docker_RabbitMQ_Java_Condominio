package com.condominiosaas.controller;

import com.condominiosaas.service.AuthService;
import com.condominiosaas.dto.AuthUserDto;
import com.condominiosaas.dto.CreateAuthUserRequest;
import com.condominiosaas.dto.UpdateAuthUserRequest;
import com.condominiosaas.service.AuthUserService;
import com.condominiosaas.dto.PagedResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

@RestController
@RequestMapping("/Auth")
public class AuthController {

	private final AuthService authService;
	private final AuthUserService authUserService;

	public AuthController(AuthService authService, AuthUserService authUserService) {
		this.authService = authService;
		this.authUserService = authUserService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthLoginRequest request) {
		var result = authService.authenticate(request.username, request.password);
		return result.map(m -> ResponseEntity
			.ok(Map.of("token", m.get("token"), "sucesso", true, "primeiroAcesso", m.get("primeiroAcesso"))))
				.orElse(ResponseEntity.status(401)
					.body(Map.of("sucesso", false, "erro", "Credenciais inválidas")));
	}

	@PostMapping("/criar-usuario")
	@PreAuthorize("hasRole('Suporte')")
	public ResponseEntity<?> criarUsuario(@RequestBody CreateAuthUserRequest request) {
		// AdminPolicy checks: require primeiroAcesso=false and status ativo - caller should satisfy; here assume Suporte
		var created = authUserService.create(request);
		return ResponseEntity.status(201)
			.body(Map.of("sucesso", true, "dados", Map.of("id", created.getId())));
	}

	@GetMapping
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Suporte')")
	public ResponseEntity<?> getAll(@RequestParam(required = false) Long empresaId) {
		var list = authUserService.getAll(empresaId);
		return ResponseEntity.ok(Map.of("sucesso", true, "dados", list));
	}

	@GetMapping("/paginado")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Suporte')")
	public ResponseEntity<?> getPaged(@RequestParam(defaultValue = "1") int page,
									  @RequestParam(defaultValue = "5") int pageSize,
									  @RequestParam(defaultValue = "Id") String sortBy,
									  @RequestParam(defaultValue = "ASC") String direction,
									  @RequestParam(required = false) Long empresaId,
									  @RequestParam(defaultValue = "") String userName) {
		var pageResult = authUserService.getPaged(page, pageSize, sortBy, direction, empresaId, userName);
		var paged = new PagedResult<AuthUserDto>(
				pageResult.getContent(),
				pageResult.getTotalElements(),
				page,
				pageSize
		);
		return ResponseEntity.ok(Map.of("sucesso", true, "dados", paged));
	}

	@GetMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Suporte')")
	public ResponseEntity<?> getById(@PathVariable java.util.UUID id) {
		return authUserService.getById(id)
			.map(u -> ResponseEntity.ok(Map.of("sucesso", true, "dados", u)))
				.orElse(ResponseEntity.status(404).body(Map.of("sucesso", false, "erro", "Não encontrado")));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Suporte')")
	public ResponseEntity<?> deleteById(@PathVariable java.util.UUID id) {
		boolean ok = authUserService.delete(id);
		return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest()
			.body(Map.of("sucesso", false, "erro", "Falha ao excluir"));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('Suporte')")
	public ResponseEntity<?> updateUsuario(@PathVariable java.util.UUID id, @RequestBody UpdateAuthUserRequest request) {
		if (!id.equals(request.getId()))
			return ResponseEntity.badRequest()
				.body(Map.of("sucesso", false, "erro", "O ID da URL não corresponde ao ID do corpo da requisição."));

		boolean ok = authUserService.update(id, request);
		return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest()
			.body(Map.of("sucesso", false, "erro", "Falha ao atualizar usuário"));
	}

	@PostMapping("/definir-senha-permanente")
	@PreAuthorize("@securityPolicies.permitirTrocaSenha(authentication)")
	public ResponseEntity<?> definirSenha(@RequestBody DefinirSenhaRequest request) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated())
			return ResponseEntity.status(401)
				.body(Map.of("sucesso", false, "erro", "Sessão inválida."));

		String username = auth.getName();

		boolean ok = authService.definirSenhaPermanente(username, request.NovaSenha);
		return ok ? ResponseEntity.ok(Map.of("sucesso", true)) : ResponseEntity.badRequest()
			.body(Map.of("sucesso", false, "erro", "Falha ao definir senha."));
	}
}

class AuthLoginRequest {
	public String username;
	public String password;
}

class DefinirSenhaRequest {
	public String NovaSenha;
}
