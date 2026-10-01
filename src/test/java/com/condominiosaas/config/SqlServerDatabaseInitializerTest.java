package com.condominiosaas.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SqlServerDatabaseInitializerTest {

	@Test
	void addsDatabaseNameWhenItIsMissing() {
		assertEquals(
				"jdbc:sqlserver://localhost:1433;encrypt=false;databaseName=Condominio_aspnet",
				SqlServerDatabaseInitializer.withDatabaseName(
						"jdbc:sqlserver://localhost:1433;encrypt=false", "Condominio_aspnet"));
	}

	@Test
	void replacesExistingDatabaseNameWithoutChangingOtherOptions() {
		assertEquals(
				"jdbc:sqlserver://localhost:1433;DATABASEname=master;encrypt=false",
				SqlServerDatabaseInitializer.withDatabaseName(
						"jdbc:sqlserver://localhost:1433;DATABASEname=AppDb;encrypt=false", "master"));
	}

	@Test
	void rejectsDatabaseNamesThatCouldBreakTheJdbcUrl() {
		assertThrows(IllegalArgumentException.class, () ->
				SqlServerDatabaseInitializer.withDatabaseName(
						"jdbc:sqlserver://localhost:1433", "db;encrypt=true"));
	}
}
