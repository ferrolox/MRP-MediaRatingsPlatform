package com.ferrolox.configuration;

import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfiguration {

	private final String jdbcUrl;
	private final String username;
	private final String password;

	public DatabaseConfiguration(String jdbcUrl, String username, String password) {
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
	}

	public void migrate() {
		Flyway.configure()
			  .dataSource(jdbcUrl, username, password)
			  .locations("classpath:migrations")
			  .load()
			  .migrate();
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
            jdbcUrl,
            username,
            password
		);
	}
}