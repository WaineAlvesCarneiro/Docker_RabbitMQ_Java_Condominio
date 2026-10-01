package com.condominiosaas.dto;

import lombok.Data;

@Data
public class CreateAuthUserRequest {
	private String userName;
	private String email;
	private String role;
	private String senha;
	private Long empresaId;
}
