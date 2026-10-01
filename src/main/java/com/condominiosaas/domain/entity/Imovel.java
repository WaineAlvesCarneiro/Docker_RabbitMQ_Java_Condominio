package com.condominiosaas.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "Imovel", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Imovel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "Bloco", length = 100, nullable = false)
	private String bloco;
	@Column(name = "Apartamento", length = 100, nullable = false)
	private String apartamento;
	@Column(name = "BoxGaragem", length = 100, nullable = false)
	private String boxGaragem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EmpresaId")
	private Empresa empresa;
	@Column(name = "DataInclusao", nullable = false)
	private LocalDateTime dataInclusao;
	@Column(name = "DataAlteracao", nullable = true)
	private LocalDateTime dataAlteracao;

	@OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Morador> moradores = new ArrayList<>();
}
