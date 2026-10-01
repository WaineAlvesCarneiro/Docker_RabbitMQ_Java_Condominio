package com.condominiosaas.controller;

import com.condominiosaas.dto.MoradorDto;
import com.condominiosaas.service.MoradorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/morador")
public class MoradorController {

	private final MoradorService moradorService;

	public MoradorController(MoradorService moradorService) {
		this.moradorService = moradorService;
	}

	@GetMapping
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and (hasRole('Sindico') or hasRole('Porteiro'))")
	public ResponseEntity<?> getAll() {
		List<MoradorDto> dados = moradorService.getAll();
		return ResponseEntity.ok().body(dados);
	}

	@GetMapping("/paginado")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and (hasRole('Sindico') or hasRole('Porteiro'))")
	public ResponseEntity<?> getPaged(@RequestParam(defaultValue = "1") int page,
									  @RequestParam(defaultValue = "5") int pageSize) {
		var list = moradorService.getAll();
		var paged = new com.condominiosaas.dto.PagedResult<>(list, list.size(), page, pageSize);
		return ResponseEntity.ok().body(paged);
	}

	@GetMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and (hasRole('Sindico') or hasRole('Porteiro'))")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		return moradorService.getById(id)
			.map(d -> ResponseEntity.ok().body(d))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Sindico')")
	public ResponseEntity<?> create(@RequestBody MoradorDto command) {
		MoradorDto created = moradorService.create(command);
		return ResponseEntity.created(URI.create("/morador/" + created.getId())).body(created);
	}

	@PutMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Sindico')")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MoradorDto command) {
		boolean ok = moradorService.update(id, command);
		return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Sindico')")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		boolean ok = moradorService.delete(id);
		return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest()
			.body(java.util.Map.of("sucesso", false, "erro", "Falha ao excluir"));
	}
}
