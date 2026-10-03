package com.condominiosaas.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Morador", schema = "dbo", indexes = {
		@Index(name = "IX_Morador_EmpresaId", columnList = "EmpresaId"),
		@Index(name = "IX_Morador_ImovelId", columnList = "ImovelId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Morador {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false)
	private Long id;

	@Nationalized
	@Column(name = "Nome", length = 255, nullable = false)
	private String nome;
	@Nationalized
	@Column(name = "Celular", length = 16, nullable = false)
	private String celular;
	@Nationalized
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

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ImovelId", nullable = false,
			foreignKey = @ForeignKey(name = "FK_Morador_Imovel_ImovelId"))
	private Imovel imovel;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EmpresaId", nullable = false,
			foreignKey = @ForeignKey(name = "FK_Morador_Empresa_EmpresaId"))
	private Empresa empresa;
}
