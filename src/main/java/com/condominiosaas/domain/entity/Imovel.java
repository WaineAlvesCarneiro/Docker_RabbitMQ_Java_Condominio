package com.condominiosaas.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Imovel", schema = "dbo", indexes = {
		@Index(name = "IX_Imovel_EmpresaId", columnList = "EmpresaId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Imovel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false)
	private Long id;

	@Nationalized
	@Column(name = "Bloco", length = 100, nullable = false)
	private String bloco;
	@Nationalized
	@Column(name = "Apartamento", length = 100, nullable = false)
	private String apartamento;
	@Nationalized
	@Column(name = "BoxGaragem", length = 100, nullable = false)
	private String boxGaragem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "EmpresaId", nullable = false,
			foreignKey = @ForeignKey(name = "FK_Imovel_Empresa_EmpresaId"))
	private Empresa empresa;

	@OneToMany(mappedBy = "imovel")
	private List<Morador> moradores = new ArrayList<>();
}
