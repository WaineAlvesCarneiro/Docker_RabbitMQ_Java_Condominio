package com.condominiosaas.mapping;

import com.condominiosaas.domain.entity.Empresa;
import com.condominiosaas.domain.entity.Imovel;
import com.condominiosaas.dto.ImovelDto;

public class ImovelMapper {

	public static Imovel toEntity(ImovelDto req) {
		Imovel i = new Imovel();
		i.setBloco(req.getBloco());
		i.setApartamento(req.getApartamento());
		i.setBoxGaragem(req.getBoxGaragem());

		Empresa e = new Empresa();
		e.setId(req.getEmpresaId());
		i.setEmpresa(e);

		return i;
	}

	public static void updateFromRequest(Imovel imovel, ImovelDto req) {
		imovel.setBloco(req.getBloco());
		imovel.setApartamento(req.getApartamento());
		imovel.setBoxGaragem(req.getBoxGaragem());
	}

	public static ImovelDto toDto(Imovel i) {
		ImovelDto d = new ImovelDto();
		d.setId(i.getId());
		d.setBloco(i.getBloco());
		d.setApartamento(i.getApartamento());
		d.setBoxGaragem(i.getBoxGaragem());
		d.setEmpresaId(i.getEmpresa().getId());
		return d;
	}
}
