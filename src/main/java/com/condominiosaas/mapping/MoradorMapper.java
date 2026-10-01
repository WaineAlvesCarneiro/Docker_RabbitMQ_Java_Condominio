package com.condominiosaas.mapping;

import com.condominiosaas.domain.entity.Empresa;
import com.condominiosaas.domain.entity.Imovel;
import com.condominiosaas.domain.entity.Morador;
import com.condominiosaas.dto.MoradorDto;

import java.time.LocalDateTime;

public class MoradorMapper {

	public static Morador toEntity(MoradorDto req) {
		Morador m = new Morador();
		m.setNome(req.getNome());
		m.setCelular(req.getCelular());
		m.setEmail(req.getEmail());
		m.setIsProprietario(req.getIsProprietario());
		m.setDataEntrada(req.getDataEntrada());
		m.setDataInclusao(LocalDateTime.now());
		m.setDataSaida(req.getDataSaida());
		m.setDataAlteracao(null);
		
		Imovel i = new Imovel();
		i.setId(req.getImovelId());
		m.setImovel(i);

		Empresa e = new Empresa();
		e.setId(req.getEmpresaId());
		m.setEmpresa(e);

		return m;
	}

	public static void updateFromRequest(Morador entidade, MoradorDto req) {
		entidade.setNome(req.getNome());
		entidade.setCelular(req.getCelular());
		entidade.setEmail(req.getEmail());
		entidade.setIsProprietario(req.getIsProprietario());
		entidade.setDataEntrada(req.getDataEntrada());
		entidade.setDataSaida(req.getDataSaida());
		// association updates handled by service layer
		entidade.setDataAlteracao(LocalDateTime.now());
	}

	public static MoradorDto toDto(Morador m) {
		MoradorDto d = new MoradorDto();
		d.setId(m.getId());
		d.setNome(m.getNome());
		d.setCelular(m.getCelular());
		d.setEmail(m.getEmail());
		d.setIsProprietario(m.getIsProprietario());
		d.setDataEntrada(m.getDataEntrada());
		d.setDataSaida(m.getDataSaida());

		if (m.getImovel() != null) d.setImovelId(m.getImovel().getId());
		if (m.getEmpresa() != null) d.setEmpresaId(m.getEmpresa().getId());
		return d;
	}
}
