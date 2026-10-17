package com.ferrolox.persistence.repositories;

import com.ferrolox.configuration.DatabaseConfiguration;
import com.ferrolox.domain.User;
import com.ferrolox.foundation.exceptions.DatabaseException;
import com.ferrolox.persistence.interfaces.UserInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository implements UserInterface {

	private final DatabaseConfiguration database;

	public UserRepository(DatabaseConfiguration database) {
		this.database = database;
	}

	@Override
	public Optional<User> findById(int id) {
		return Optional.empty();
	}

	@Override
	public Optional<User> findByUsername(String username) throws DatabaseException {
		String sql = """
				SELECT id, username, alias, password_hash
			    FROM users
			    WHERE username = ?;
			""";

		try (
			Connection connection = database.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)
		) {
			statement.setString(1, username);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) return Optional.empty();

				User user = new User(
					resultSet.getInt("id"),
					resultSet.getString("username"),
					resultSet.getString("alias"),
					resultSet.getString("password_hash")
				);

				return Optional.of(user);
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to find user", exception);
		}
	}

	@Override
	public Integer save(User user) throws DatabaseException {
		String sql = """
				INSERT INTO users (username, alias, password_hash)
				VALUES (?, ?, ?)
				RETURNING id;
			""";

		try (
			Connection connection = database.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)
		) {
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getAlias());
			statement.setString(3, user.getPassword());

			try (ResultSet resultSet = statement.executeQuery()) {
				resultSet.next();

				return resultSet.getInt("id");
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to register user", exception);
		}
	}

	@Override
	public void update(User user) {

	}

	@Override
	public void delete(int id) {

	}
}