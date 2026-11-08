package com.ferrolox.persistence.interfaces;

import com.ferrolox.domain.User;
import com.ferrolox.foundation.exceptions.DatabaseException;

import java.util.Optional;

public interface UserInterface {

	Optional<User> findById(int id) throws DatabaseException;

	Optional<User> findByUsername(String username) throws DatabaseException;

	int save(User user) throws DatabaseException;

	void update(User user) throws DatabaseException;

	void deleteById(int id) throws DatabaseException;
}