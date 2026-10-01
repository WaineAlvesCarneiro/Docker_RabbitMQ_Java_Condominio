package com.condominiosaas.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Morador", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Morador {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "Nome", length = 255, nullable = false)
	private String nome;
	@Column(name = "Celular", length = 16, nullable = false)
	private String celular;
	@Column(name = "Email", length = 255, nullable = false)
	private String email;
	@Column(name = "IsProprietario", nullable = false)
	private Boolean isProprietario;
	@Column(name = "DataEntrada", nullable = false)
	private LocalDate dataEntrada;
	@Column(name = "DataSaida")
	private LocalDate dataSaida;
	@Column(name = "DataInclusao", nullable = false)
	private LocalDateTime dataInclusao;
	@Column(name = "DataAlteracao", nullable = true)
	private LocalDateTime dataAlteracao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ImovelId")
	private Imovel imovel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EmpresaId")
	private Empresa empresa;
}
