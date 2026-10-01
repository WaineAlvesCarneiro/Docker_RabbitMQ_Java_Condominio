package com.condominiosaas.controller;

import com.condominiosaas.dto.ImovelDto;
import com.condominiosaas.security.CustomUserDetails;
import com.condominiosaas.service.ImovelService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.condominiosaas.dto.PagedResult;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/Imovel")
public class ImovelController {

	private final ImovelService imovelService;

	public ImovelController(ImovelService imovelService) {
		this.imovelService = imovelService;
	}

	@GetMapping
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and (hasRole('Sindico') or hasRole('Porteiro'))")
	public ResponseEntity<?> getAll(@RequestParam(required = false) Long empresaId) {
		List<ImovelDto> dados = imovelService.getAll(empresaId);
		return ResponseEntity.ok().body(java.util.Map.of("sucesso", true, "dados", dados));
	}

	@GetMapping("/paginado")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and (hasRole('Sindico') or hasRole('Porteiro'))")
	public ResponseEntity<?> getPaged(@RequestParam(defaultValue = "1") int page,
									  @RequestParam(defaultValue = "5") int pageSize,
									  @RequestParam(defaultValue = "Id") String sortBy,
									  @RequestParam(defaultValue = "ASC") String direction,
									  @RequestParam(required = false) Long empresaId,
									  @RequestParam(defaultValue = "") String bloco,
									  @RequestParam(defaultValue = "") String apartamento) {
		Page<ImovelDto> dados = imovelService.getPaged(page, pageSize, sortBy, direction, empresaId, bloco, apartamento);
		var paged = new PagedResult<ImovelDto>(dados.getContent(), dados.getTotalElements(), page, pageSize);
		return ResponseEntity.ok().body(java.util.Map.of("sucesso", true, "dados", paged));
	}

	@GetMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and (hasRole('Sindico') or hasRole('Porteiro'))")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		return imovelService.getById(id)
			.map(d -> ResponseEntity.ok().body(java.util.Map.of("sucesso", true, "dados", d)))
				.orElse(ResponseEntity.status(404).body(java.util.Map.of("sucesso", false, "erro", "Não encontrado")));
	}

	@PostMapping
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Sindico')")
	public ResponseEntity<?> create(@RequestBody ImovelDto command) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long empresaId = null;
		if (principal instanceof CustomUserDetails cud) empresaId = cud.getEmpresaId();

		var created = imovelService.create(command, empresaId);
		return ResponseEntity.created(URI.create("/Imovel/" + created.getId()))
			.body(java.util.Map.of("sucesso", true, "dados", created));
	}

	@PutMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Sindico')")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ImovelDto command) {
		if (!id.equals(command.getId()))
			return ResponseEntity.badRequest().body("O ID da URL não corresponde ao ID do corpo da requisição.");

		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long empresaId = null;
		if (principal instanceof CustomUserDetails cud) empresaId = cud.getEmpresaId();

		boolean ok = imovelService.update(id, command, empresaId);
		return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest()
			.body(java.util.Map.of("sucesso", false, "erro", "Falha ao atualizar"));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("@securityPolicies.isAdminPolicy(authentication) and hasRole('Sindico')")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		boolean ok = imovelService.delete(id);
		return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest()
			.body(java.util.Map.of("sucesso", false, "erro", "Falha ao excluir"));
	}
}
