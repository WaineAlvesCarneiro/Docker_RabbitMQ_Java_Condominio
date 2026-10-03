package com.condominiosaas.domain.entity;

import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.domain.enums.TipoUserAtivo;
import com.condominiosaas.domain.converter.TipoEmpresaAtivoConverter;
import com.condominiosaas.domain.converter.TipoRoleConverter;
import com.condominiosaas.domain.converter.TipoUserAtivoConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DynamicInsert
@Table(name = "AuthUsers", schema = "dbo", indexes = {
		@Index(name = "IX_AuthUsers_EmpresaId", columnList = "EmpresaId"),
		@Index(name = "IX_AuthUsers_UserName", columnList = "UserName", unique = true),
		@Index(name = "IX_AuthUsers_PrimeiroAcesso", columnList = "PrimeiroAcesso")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "Id", nullable = false)
	@ColumnDefault("NEWSEQUENTIALID()")
	private UUID id;

	@Nationalized
	@Column(name = "UserName", length = 100, nullable = false)
	private String userName;

	@Nationalized
	@Column(name = "Email", length = 255, nullable = false)
	private String email;

	@Column(name = "PrimeiroAcesso", nullable = false)
	private Boolean primeiroAcesso;

	@Nationalized
	@Column(name = "PasswordHash", length = 200, nullable = false)
	private String passwordHash;

	@Convert(converter = TipoRoleConverter.class)
	@Column(name = "Role", nullable = false, columnDefinition = "int")
	private String role;

	@Convert(converter = TipoUserAtivoConverter.class)
	@Column(name = "Ativo", nullable = false, columnDefinition = "int")
	@ColumnDefault("1")
	private TipoUserAtivo ativo = TipoUserAtivo.Ativo;

	@Convert(converter = TipoEmpresaAtivoConverter.class)
	@Column(name = "EmpresaAtiva", nullable = false, columnDefinition = "int")
	@ColumnDefault("1")
	private TipoEmpresaAtivo empresaAtiva = TipoEmpresaAtivo.Ativo;

	@Column(name = "DataInclusao", nullable = false)
	private LocalDateTime dataInclusao;

	@Column(name = "DataAlteracao", nullable = true)
	private LocalDateTime dataAlteracao;

	@Column(name = "EmpresaId", nullable = true)
	private Long empresaId;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EmpresaId", insertable = false, updatable = false, nullable = true,
			foreignKey = @ForeignKey(name = "FK_AuthUsers_Empresa_EmpresaId"))
	private Empresa empresa;
}
