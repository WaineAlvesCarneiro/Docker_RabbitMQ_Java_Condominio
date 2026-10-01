package com.condominiosaas.domain.entity;

import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.domain.enums.TipoUserAtivo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "AuthUsers", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
	// Confirmação: campos com anotações existentes; sem mudanças de valores.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(name = "UserName", length = 100, nullable = false)
	private String userName;

	@Column(name = "Email", length = 255, nullable = false)
	private String email;

	@Column(name = "PrimeiroAcesso")
	private Boolean primeiroAcesso;

	@Column(name = "PasswordHash", length = 200, nullable = false)
	private String passwordHash;

	@Column(name = "Role", nullable = false)
	private String role;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "Ativo", nullable = false)
	private TipoUserAtivo ativo;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "EmpresaAtiva", nullable = false)
	private TipoEmpresaAtivo empresaAtiva;

	@Column(name = "DataInclusao", nullable = false)
	private LocalDateTime dataInclusao;

	@Column(name = "DataAlteracao", nullable = true)
	private LocalDateTime dataAlteracao;

	@Column(name = "EmpresaId", nullable = true)
	private Long empresaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EmpresaId", insertable = false, updatable = false)
	private Empresa empresa;
}
