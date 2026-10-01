package com.condominiosaas.domain.entity;

import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.domain.enums.TipoCondominio;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Empresa", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "RazaoSocial", length = 100, nullable = false)
	private String razaoSocial;
	@Column(name = "Fantasia", length = 100, nullable = false)
	private String fantasia;
	@Column(name = "Cnpj", length = 18, nullable = false)
	private String cnpj;
	@Enumerated(EnumType.ORDINAL)
	private TipoCondominio tipoDeCondominio;
	@Enumerated(EnumType.ORDINAL)
	private TipoEmpresaAtivo ativo;
	private String nome;
	private String celular;
	private String telefone;
	private String email;
	private String senha;
	private String host;
	private Integer porta;
	private String cep;
	private String uf;
	private String cidade;
	private String endereco;
	private String bairro;
	private String complemento;
	@Column(name = "DataInclusao", nullable = false)
	private LocalDateTime dataInclusao;
	@Column(name = "DataAlteracao", nullable = true)
	private LocalDateTime dataAlteracao;

	@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AuthUser> authUsers = new ArrayList<>();

	@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Imovel> imoveis = new ArrayList<>();

	@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Morador> moradores = new ArrayList<>();
}
