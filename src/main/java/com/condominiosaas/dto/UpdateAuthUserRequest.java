package com.condominiosaas.dto;

import lombok.Data;

import java.util.UUID;
import com.condominiosaas.domain.enums.TipoUserAtivo;
import com.condominiosaas.domain.enums.TipoEmpresaAtivo;

@Data
public class UpdateAuthUserRequest {
	private UUID id;
	private String userName;
	private String email;
	private String role;
	private String senha;
	private Long empresaId;
	private TipoUserAtivo ativo;
	private TipoEmpresaAtivo empresaAtiva;
}
