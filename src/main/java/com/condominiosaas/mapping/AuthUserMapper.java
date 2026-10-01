package com.condominiosaas.mapping;

import com.condominiosaas.domain.entity.AuthUser;
import com.condominiosaas.domain.entity.Empresa;
import com.condominiosaas.dto.AuthUserDto;
import com.condominiosaas.dto.CreateAuthUserRequest;
import com.condominiosaas.dto.UpdateAuthUserRequest;
import com.condominiosaas.domain.enums.TipoUserAtivo;
import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.domain.enums.TipoRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthUserMapper {

	public static AuthUser toEntity(CreateAuthUserRequest req, String senhaTemporaria, String passwordHash) {
		AuthUser u = new AuthUser();
		u.setId(UUID.randomUUID());

		Empresa e = new Empresa();
		e.setId(req.getEmpresaId());
		u.setEmpresa(e);
		u.setEmpresaId(req.getEmpresaId());

		u.setUserName(req.getUserName());
		u.setEmail(req.getEmail());
		u.setPrimeiroAcesso(true);
		u.setPasswordHash(passwordHash);
		u.setRole(req.getRole());
		u.setDataInclusao(LocalDateTime.now());
		u.setDataAlteracao(null);
		u.setAtivo(TipoUserAtivo.Ativo);
		u.setEmpresaAtiva(TipoEmpresaAtivo.Ativo);
		return u;
	}

	public static void updateFromRequest(AuthUser entidade, UpdateAuthUserRequest req) {
		entidade.setUserName(req.getUserName());
		entidade.setEmail(req.getEmail());
		if (req.getRole() != null) entidade.setRole(req.getRole());
		if (req.getAtivo() != null) entidade.setAtivo(req.getAtivo());
		if (req.getEmpresaAtiva() != null) entidade.setEmpresaAtiva(req.getEmpresaAtiva());
		entidade.setDataAlteracao(LocalDateTime.now());
	}

	public static AuthUserDto toDto(AuthUser dado) {
		AuthUserDto d = new AuthUserDto();
		d.setId(dado.getId());
		d.setAtivo(dado.getAtivo());
		d.setEmpresaAtiva(dado.getEmpresaAtiva());
		d.setEmpresaId(dado.getEmpresaId());
		d.setUserName(dado.getUserName());
		d.setEmail(dado.getEmail());
		d.setPrimeiroAcesso(dado.getPrimeiroAcesso() != null ? dado.getPrimeiroAcesso() : false);
		try { d.setRole(TipoRole.valueOf(dado.getRole())); } catch (Exception ex) { d.setRole(null); }

		return d;
	}
}
