package com.condominiosaas.dto;

import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.domain.enums.TipoRole;
import com.condominiosaas.domain.enums.TipoUserAtivo;
import lombok.Data;

import java.util.UUID;

@Data
public class AuthUserDto {
	private UUID id;
	private TipoUserAtivo ativo;
	private TipoEmpresaAtivo empresaAtiva;
	private Long empresaId;
	private Object empresaDto; // optional, can be mapped later
	private String userName;
	private String email;
	private boolean primeiroAcesso;
	private TipoRole role;
}
