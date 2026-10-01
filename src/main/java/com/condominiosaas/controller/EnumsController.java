package com.condominiosaas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.condominiosaas.domain.enums.TipoCondominio;
import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.domain.enums.TipoRole;
import com.condominiosaas.domain.enums.TipoUserAtivo;
import com.condominiosaas.security.SecurityPolicies;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Enums")
public class EnumsController {

	// delega verificação ao componente centralizado
	@SuppressWarnings("unused")
	private final SecurityPolicies securityPolicies;

	public EnumsController(SecurityPolicies securityPolicies) {
		this.securityPolicies = securityPolicies;
	}

	private List<Map<String, Object>> toOptions(Class<? extends Enum<?>> e) {
		return Arrays.stream(e.getEnumConstants())
			.map(en -> {
				Map<String, Object> m = new java.util.HashMap<>();
				m.put("Value", en.ordinal());
				m.put("Label", en.name());
				return m;
			})
			.collect(Collectors.toList());
	}

	@GetMapping("/tipo-condominio")
	@org.springframework.security.access.prepost.PreAuthorize("@securityPolicies.isAdminPolicy(authentication)")
	public ResponseEntity<?> getTipoCondominio() {
		return ResponseEntity.ok(toOptions(TipoCondominio.class));
	}

	@GetMapping("/tipo-role")
	@org.springframework.security.access.prepost.PreAuthorize("@securityPolicies.isAdminPolicy(authentication)")
	public ResponseEntity<?> getTipoRole() {
		return ResponseEntity.ok(toOptions(TipoRole.class));
	}

	@GetMapping("/tipo-user-ativo")
	@org.springframework.security.access.prepost.PreAuthorize("@securityPolicies.isAdminPolicy(authentication)")
	public ResponseEntity<?> getTipoUserAtivo() {
		return ResponseEntity.ok(toOptions(TipoUserAtivo.class));
	}

	@GetMapping("/tipo-empresa-ativo")
	@org.springframework.security.access.prepost.PreAuthorize("@securityPolicies.isAdminPolicy(authentication)")
	public ResponseEntity<?> getTipoEmpresaAtivo() {
		return ResponseEntity.ok(toOptions(TipoEmpresaAtivo.class));
	}
}
