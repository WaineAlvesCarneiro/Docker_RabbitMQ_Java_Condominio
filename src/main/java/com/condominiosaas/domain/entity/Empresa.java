package com.condominiosaas.domain.entity;

import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.domain.enums.TipoCondominio;
import com.condominiosaas.domain.converter.TipoCondominioConverter;
import com.condominiosaas.domain.converter.TipoEmpresaAtivoConverter;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicInsert
@Table(name = "Empresa", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false)
	private Long id;

	@Nationalized
	@Column(name = "RazaoSocial", length = 100, nullable = false)
	private String razaoSocial;
	@Nationalized
	@Column(name = "Fantasia", length = 100, nullable = false)
	private String fantasia;
	@Nationalized
	@Column(name = "Cnpj", length = 18, nullable = false)
	private String cnpj;
	@Convert(converter = TipoCondominioConverter.class)
	@Column(name = "TipoDeCondominio", nullable = false, columnDefinition = "int")
	private TipoCondominio tipoDeCondominio;
	@Convert(converter = TipoEmpresaAtivoConverter.class)
	@Column(name = "Ativo", nullable = false, columnDefinition = "int")
	@ColumnDefault("1")
	private TipoEmpresaAtivo ativo = TipoEmpresaAtivo.Ativo;
	@Nationalized
	@Column(name = "Nome", length = 255, nullable = false)
	private String nome;
	@Nationalized
	@Column(name = "Celular", length = 16, nullable = false)
	private String celular;
	@Nationalized
	@Column(name = "Telefone", length = 15)
	private String telefone;
	@Nationalized
	@Column(name = "Email", length = 255, nullable = false)
	private String email;
	@Nationalized
	@Column(name = "Senha", length = 255)
	private String senha;
	@Nationalized
	@Column(name = "Host", length = 100, nullable = false)
	private String host;
	@Column(name = "Porta", nullable = false)
	private Integer porta;
	@Nationalized
	@Column(name = "Cep", length = 14, nullable = false)
	private String cep;
	@Nationalized
	@Column(name = "Uf", length = 100, nullable = false)
	private String uf;
	@Nationalized
	@Column(name = "Cidade", length = 150, nullable = false)
	private String cidade;
	@Nationalized
	@Column(name = "Endereco", length = 150, nullable = false)
	private String endereco;
	@Nationalized
	@Column(name = "Bairro", nullable = false, columnDefinition = "nvarchar(max)")
	private String bairro;
	@Nationalized
	@Column(name = "Complemento", length = 255)
	private String complemento;
	@Column(name = "DataInclusao", nullable = false)
	private LocalDateTime dataInclusao;
	@Column(name = "DataAlteracao", nullable = true)
	private LocalDateTime dataAlteracao;

	@OneToMany(mappedBy = "empresa")
	private List<AuthUser> authUsers = new ArrayList<>();

	@OneToMany(mappedBy = "empresa")
	private List<Imovel> imoveis = new ArrayList<>();

	@OneToMany(mappedBy = "empresa")
	private List<Morador> moradores = new ArrayList<>();
}
