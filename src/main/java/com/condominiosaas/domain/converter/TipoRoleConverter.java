package com.condominiosaas.domain.converter;

import com.condominiosaas.domain.enums.TipoRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TipoRoleConverter implements AttributeConverter<String, Integer> {

	@Override
	public Integer convertToDatabaseColumn(String role) {
		if (role == null) {
			return null;
		}
		return switch (TipoRole.valueOf(role)) {
			case Suporte -> 1;
			case Sindico -> 2;
			case Porteiro -> 3;
		};
	}

	@Override
	public String convertToEntityAttribute(Integer value) {
		if (value == null) {
			return null;
		}
		return switch (value) {
			case 1 -> TipoRole.Suporte.name();
			case 2 -> TipoRole.Sindico.name();
			case 3 -> TipoRole.Porteiro.name();
			default -> throw new IllegalArgumentException("Código TipoRole inválido no banco: " + value);
		};
	}
}
