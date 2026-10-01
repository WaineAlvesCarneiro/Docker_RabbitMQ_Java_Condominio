package com.condominiosaas.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SqlServerDatabaseInitializer {

	private static final Pattern DATABASE_NAME_PARAMETER =
			Pattern.compile("(?i)(;databaseName=)[^;]*");

	private SqlServerDatabaseInitializer() {
	}

	static String withDatabaseName(String jdbcUrl, String databaseName) {
		if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:sqlserver://")) {
			throw new IllegalArgumentException("A URL do datasource precisa ser uma URL JDBC do SQL Server.");
		}
		validateDatabaseName(databaseName);

		Matcher matcher = DATABASE_NAME_PARAMETER.matcher(jdbcUrl);
		if (matcher.find()) {
			return matcher.replaceFirst(
					Matcher.quoteReplacement(matcher.group(1) + databaseName));
		}
		return jdbcUrl + ";databaseName=" + databaseName;
	}

	static void createIfMissing(
			String targetUrl, String username, String password, String databaseName) {
		validateDatabaseName(databaseName);
		String masterUrl = withDatabaseName(targetUrl, "master");
		String escapedName = databaseName.replace("'", "''");
		String quotedName = "[" + databaseName.replace("]", "]]") + "]";
		String createDatabaseSql = "IF DB_ID(N'" + escapedName
				+ "') IS NULL CREATE DATABASE " + quotedName;

		try (Connection connection = DriverManager.getConnection(masterUrl, username, password)) {
			try (Statement statement = connection.createStatement()) {
				statement.execute(createDatabaseSql);
			} catch (SQLException creationException) {
				if (databaseExists(connection, databaseName)) {
					return;
				}
				throw creationException;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException(
					"Não foi possível criar ou verificar o banco de dados '" + databaseName
							+ "'. Verifique a conexão com o SQL Server e a permissão CREATE DATABASE.",
					exception);
		}
	}

	private static boolean databaseExists(Connection connection, String databaseName)
			throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("SELECT DB_ID(?)")) {
			statement.setString(1, databaseName);
			try (var resultSet = statement.executeQuery()) {
				return resultSet.next() && resultSet.getObject(1) != null;
			}
		}
	}

	private static void validateDatabaseName(String databaseName) {
		if (databaseName == null || databaseName.isBlank() || databaseName.length() > 128
				|| databaseName.matches(".*[\\\\/:*?\"<>|;=\\p{Cntrl}].*")) {
			throw new IllegalArgumentException(
					"O nome do banco de dados é inválido para o SQL Server ou para a URL JDBC.");
		}
	}
}
