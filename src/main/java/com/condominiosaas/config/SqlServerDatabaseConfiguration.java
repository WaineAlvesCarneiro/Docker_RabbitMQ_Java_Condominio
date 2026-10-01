package com.condominiosaas.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
public class SqlServerDatabaseConfiguration {

	@Bean
	@Primary
	DataSource dataSource(
			DataSourceProperties properties,
			Environment environment) {
		String url = properties.getUrl();
		boolean createIfMissing = environment.getProperty(
				"app.database.create-if-missing", Boolean.class, true);

		if (createIfMissing) {
			String databaseName = environment.getProperty(
					"app.database.name", "Condominio_aspnet");
			url = SqlServerDatabaseInitializer.withDatabaseName(url, databaseName);
			SqlServerDatabaseInitializer.createIfMissing(
					url, properties.getUsername(), properties.getPassword(), databaseName);
		}

		return properties.initializeDataSourceBuilder()
				.url(url)
				.build();
	}
}
