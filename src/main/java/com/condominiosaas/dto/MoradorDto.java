package com.condominiosaas.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MoradorDto {
	private Long id;
	private String nome;
	private String celular;
	private String email;
	private Boolean isProprietario;
	private LocalDate dataEntrada;
	private LocalDate dataSaida;
	private Long imovelId;
	private Long empresaId;
}
