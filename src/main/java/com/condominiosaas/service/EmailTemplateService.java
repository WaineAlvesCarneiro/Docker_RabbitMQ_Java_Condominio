package com.condominiosaas.service;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {
	public String gerarBoasVindasEmpresa(String razaoSocial) {
		return String.format("<p>Olá %s,</p><p>Bem-vindo ao Sistema.</p>", razaoSocial);
	}

	public String gerarEmpresaAlterada(String razaoSocial) {
		return String.format("<p>Informamos que os dados da empresa %s foram atualizados.</p>", razaoSocial);
	}

	public String gerarBoasVindasUsuario(String userName, String senhaTemporaria) {
		return String.format("<p>Olá %s,</p><p>Sua senha temporária é: <b>%s</b></p>", userName, senhaTemporaria);
	}

	public String gerarUsuarioAlterado(String userName) {
		return String.format("<p>Informamos que os dados do usuário %s foram atualizados.</p>", userName);
	}

	public String gerarBoasVindasMorador(String nome) {
		return String.format("<p>Olá %s,</p><p>Bem-vindo ao Condomínio.</p>", nome);
	}

	public String gerarMoradorAlterado(String nome) {
		return String.format("<p>Informamos que os dados do morador %s foram atualizados.</p>", nome);
	}
}
