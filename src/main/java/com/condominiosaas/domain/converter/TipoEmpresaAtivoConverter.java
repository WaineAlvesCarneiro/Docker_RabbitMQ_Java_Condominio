package com.condominiosaas.domain.converter;

import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TipoEmpresaAtivoConverter implements AttributeConverter<TipoEmpresaAtivo, Integer> {

	@Override
	public Integer convertToDatabaseColumn(TipoEmpresaAtivo value) {
		return value == null ? null : value.ordinal() + 1;
	}

	@Override
	public TipoEmpresaAtivo convertToEntityAttribute(Integer value) {
		return value == null ? null : fromCode(value, TipoEmpresaAtivo.values());
	}

	private static TipoEmpresaAtivo fromCode(int code, TipoEmpresaAtivo[] values) {
		if (code < 1 || code > values.length) {
			throw new IllegalArgumentException("Código TipoEmpresaAtivo inválido no banco: " + code);
		}
		return values[code - 1];
	}
}
