package com.condominiosaas.domain.converter;

import com.condominiosaas.domain.enums.TipoUserAtivo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TipoUserAtivoConverter implements AttributeConverter<TipoUserAtivo, Integer> {

	@Override
	public Integer convertToDatabaseColumn(TipoUserAtivo value) {
		return value == null ? null : value.ordinal() + 1;
	}

	@Override
	public TipoUserAtivo convertToEntityAttribute(Integer value) {
		return value == null ? null : fromCode(value, TipoUserAtivo.values());
	}

	private static TipoUserAtivo fromCode(int code, TipoUserAtivo[] values) {
		if (code < 1 || code > values.length) {
			throw new IllegalArgumentException("Código TipoUserAtivo inválido no banco: " + code);
		}
		return values[code - 1];
	}
}
