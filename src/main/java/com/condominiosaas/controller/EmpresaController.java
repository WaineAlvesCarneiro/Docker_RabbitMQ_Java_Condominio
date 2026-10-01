package com.condominiosaas.controller;

import com.condominiosaas.dto.EmpresaDto;
import com.condominiosaas.service.EmpresaService;
import com.condominiosaas.dto.PagedResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/empresa")
public class EmpresaController {

	private final EmpresaService empresaService;

	public EmpresaController(EmpresaService empresaService) {
		this.empresaService = empresaService;
	}

	@GetMapping
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and (hasRole('Suporte') or hasRole('Sindico') )")
	public ResponseEntity<?> getAll() {
		List<EmpresaDto> dados = empresaService.getAll();
		return ResponseEntity.ok().body(dados);
	}

	@GetMapping("/paginado")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Suporte')")
	public ResponseEntity<?> getPaged(@RequestParam(defaultValue = "1") int page,
									  @RequestParam(defaultValue = "5") int pageSize) {
		// implementar paginação futura; por enquanto converte lista para PagedResult simples
		var list = empresaService.getAll();
		var paged = new PagedResult<>(list, list.size(), page, pageSize);
		return ResponseEntity.ok().body(paged);
	}

	@GetMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Suporte')")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		return empresaService.getById(id)
			.map(d -> ResponseEntity.ok().body(d))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Suporte')")
	public ResponseEntity<?> create(@RequestBody EmpresaDto command) {
		EmpresaDto created = empresaService.create(command);
		return ResponseEntity.created(URI.create("/empresa/" + created.getId())).body(created);
	}

	@PutMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Suporte')")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EmpresaDto command) {
		boolean ok = empresaService.update(id, command);
		return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Suporte')")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		boolean ok = empresaService.delete(id);
		return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
	}
}
