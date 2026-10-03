package com.condominiosaas.domain.converter;

import com.condominiosaas.domain.enums.TipoCondominio;
import com.condominiosaas.domain.enums.TipoEmpresaAtivo;
import com.condominiosaas.domain.enums.TipoRole;
import com.condominiosaas.domain.enums.TipoUserAtivo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatabaseEnumConvertersTest {

	@Test
	void persistsEnumsUsingAspNetCoreOneBasedCodes() {
		assertEquals(1, new TipoUserAtivoConverter().convertToDatabaseColumn(TipoUserAtivo.Ativo));
		assertEquals(1, new TipoEmpresaAtivoConverter().convertToDatabaseColumn(TipoEmpresaAtivo.Ativo));
		assertEquals(1, new TipoCondominioConverter().convertToDatabaseColumn(TipoCondominio.Casas));
		assertEquals(1, new TipoRoleConverter().convertToDatabaseColumn(TipoRole.Suporte.name()));
	}

	@Test
	void readsAspNetCoreOneBasedEnumCodes() {
		assertEquals(TipoUserAtivo.Ativo, new TipoUserAtivoConverter().convertToEntityAttribute(1));
		assertEquals(TipoEmpresaAtivo.Ativo, new TipoEmpresaAtivoConverter().convertToEntityAttribute(1));
		assertEquals(TipoCondominio.Casas, new TipoCondominioConverter().convertToEntityAttribute(1));
		assertEquals(TipoRole.Suporte.name(), new TipoRoleConverter().convertToEntityAttribute(1));
		assertEquals(TipoRole.Porteiro.name(), new TipoRoleConverter().convertToEntityAttribute(3));
	}

	@Test
	void rejectsUnknownRoleNamesAndCodes() {
		assertThrows(IllegalArgumentException.class,
				() -> new TipoRoleConverter().convertToDatabaseColumn("Administrador"));
		assertThrows(IllegalArgumentException.class,
				() -> new TipoRoleConverter().convertToEntityAttribute(0));
	}
}
