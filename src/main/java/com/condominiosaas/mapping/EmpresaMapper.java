package com.condominiosaas.mapping;

import com.condominiosaas.domain.entity.Empresa;
import com.condominiosaas.dto.EmpresaDto;
import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.util.EncryptionHelper;

import java.time.LocalDateTime;

public class EmpresaMapper {

	public static Empresa toEntity(EmpresaDto req) {
		Empresa e = new Empresa();
		e.setRazaoSocial(req.getRazaoSocial());
		e.setFantasia(req.getFantasia());
		e.setCnpj(req.getCnpj() != null ? req.getCnpj()
			.replace(".", "").replace("-", "").replace("/", "") : "");
		e.setTipoDeCondominio(req.getTipoCondominio() != null ? req.getTipoCondominio() : null);
		e.setNome(req.getNome());
		e.setCelular(req.getCelular());
		e.setTelefone(req.getTelefone());
		e.setEmail(req.getEmail());
		e.setSenha(req.getSenha() != null && !req.getSenha().isEmpty() ? EncryptionHelper.encrypt(req.getSenha()) : null);
		e.setHost(req.getHost());
		e.setPorta(req.getPorta());
		e.setCep(req.getCep());
		e.setUf(req.getUf());
		e.setCidade(req.getCidade());
		e.setEndereco(req.getEndereco());
		e.setBairro(req.getBairro());
		e.setComplemento(req.getComplemento());
		e.setDataInclusao(LocalDateTime.now());
		e.setDataAlteracao(null);
		e.setAtivo(req.getAtivo() != null ? req.getAtivo() : TipoEmpresaAtivo.Ativo);
		return e;
	}

	public static boolean updateFromRequest(Empresa empresa, EmpresaDto req) {
		boolean statusMudouParaInativo = empresa.getAtivo() == TipoEmpresaAtivo.Ativo && req.getAtivo() != TipoEmpresaAtivo.Ativo;

		if (req.getSenha() != null && !req.getSenha().isEmpty())
			empresa.setSenha(EncryptionHelper.encrypt(req.getSenha()));

		empresa.setAtivo(req.getAtivo());
		empresa.setRazaoSocial(req.getRazaoSocial());
		empresa.setFantasia(req.getFantasia());
		empresa.setCnpj(req.getCnpj() != null ? req.getCnpj()
			.replace(".", "").replace("-", "").replace("/", "") : "");
		empresa.setTipoDeCondominio(req.getTipoCondominio() != null ? req.getTipoCondominio() : null);
		empresa.setNome(req.getNome());
		empresa.setCelular(req.getCelular());
		empresa.setTelefone(req.getTelefone());
		empresa.setEmail(req.getEmail());
		empresa.setHost(req.getHost());
		empresa.setPorta(req.getPorta());
		empresa.setCep(req.getCep());
		empresa.setUf(req.getUf());
		empresa.setCidade(req.getCidade());
		empresa.setEndereco(req.getEndereco());
		empresa.setBairro(req.getBairro());
		empresa.setComplemento(req.getComplemento());
		empresa.setDataAlteracao(LocalDateTime.now());

		return statusMudouParaInativo;
	}

	public static EmpresaDto toDto(Empresa e) {
		EmpresaDto d = new EmpresaDto();
		d.setId(e.getId());
		d.setRazaoSocial(e.getRazaoSocial());
		d.setFantasia(e.getFantasia());
		d.setCnpj(e.getCnpj());
		d.setNome(e.getNome());
		d.setCelular(e.getCelular());
		d.setEmail(e.getEmail());
		d.setSenha(null);
		return d;
	}
}
