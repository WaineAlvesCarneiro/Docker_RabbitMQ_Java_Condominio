package com.condominiosaas.dto;

import lombok.Data;

@Data
public class ImovelDto {
	private Long id;
	private String bloco;
	private String apartamento;
	private String boxGaragem;
	private Long empresaId;
}
