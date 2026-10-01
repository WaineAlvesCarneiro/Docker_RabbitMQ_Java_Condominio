package com.condominiosaas.dto;

import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.domain.enums.TipoCondominio;

import lombok.Data;

@Data
public class EmpresaDto {
	private Long id;
	private String razaoSocial;
	private String fantasia;
	private String cnpj;
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
	private TipoEmpresaAtivo ativo;
	private TipoCondominio tipoCondominio;
}
