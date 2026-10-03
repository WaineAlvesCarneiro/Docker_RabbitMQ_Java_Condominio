package com.condominiosaas.config;

import org.hibernate.boot.model.naming.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TablePreservingPhysicalNamingStrategyTest {

	private final TablePreservingPhysicalNamingStrategy strategy =
			new TablePreservingPhysicalNamingStrategy();

	@Test
	void preservesExplicitTableName() {
		assertEquals(
				"AuthUsers",
				strategy.toPhysicalTableName(Identifier.toIdentifier("AuthUsers"), null).getText());
	}

	@Test
	void preservesExplicitEntityTableNames() {
		assertEquals("Empresa",
				strategy.toPhysicalTableName(Identifier.toIdentifier("Empresa"), null).getText());
		assertEquals("Imovel",
				strategy.toPhysicalTableName(Identifier.toIdentifier("Imovel"), null).getText());
		assertEquals("Morador",
				strategy.toPhysicalTableName(Identifier.toIdentifier("Morador"), null).getText());
	}

	@Test
	void preservesExplicitColumnNames() {
		assertEquals(
				"PrimeiroAcesso",
				strategy.toPhysicalColumnName(Identifier.toIdentifier("PrimeiroAcesso"), null).getText());
		assertEquals(
				"EmpresaId",
				strategy.toPhysicalColumnName(Identifier.toIdentifier("EmpresaId"), null).getText());
	}
}
