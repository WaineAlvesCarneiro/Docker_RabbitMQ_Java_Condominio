package com.condominiosaas.domain.converter;

import com.condominiosaas.domain.enums.TipoCondominio;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TipoCondominioConverter implements AttributeConverter<TipoCondominio, Integer> {

	@Override
	public Integer convertToDatabaseColumn(TipoCondominio value) {
		return value == null ? null : value.ordinal() + 1;
	}

	@Override
	public TipoCondominio convertToEntityAttribute(Integer value) {
		if (value == null) {
			return null;
		}
		if (value < 1 || value > TipoCondominio.values().length) {
			throw new IllegalArgumentException("Código TipoCondominio inválido no banco: " + value);
		}
		return TipoCondominio.values()[value - 1];
	}
}
