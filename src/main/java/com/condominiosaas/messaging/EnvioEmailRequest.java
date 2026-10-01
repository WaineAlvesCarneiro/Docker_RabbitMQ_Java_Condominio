package com.condominiosaas.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnvioEmailRequest {
	@JsonProperty("para")
	private String para;
	@JsonProperty("assunto")
	private String assunto;
	@JsonProperty("corpo")
	private String corpo;
	@JsonProperty("empresaId")
	private Long empresaId;

	public EnvioEmailRequest() {}

	public EnvioEmailRequest(String para, String assunto, String corpo, Long empresaId) {
		this.para = para;
		this.assunto = assunto;
		this.corpo = corpo;
		this.empresaId = empresaId;
	}

	public String getPara() { return para; }
	public void setPara(String para) { this.para = para; }
	public String getAssunto() { return assunto; }
	public void setAssunto(String assunto) { this.assunto = assunto; }
	public String getCorpo() { return corpo; }
	public void setCorpo(String corpo) { this.corpo = corpo; }
	public Long getEmpresaId() { return empresaId; }
	public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
}
