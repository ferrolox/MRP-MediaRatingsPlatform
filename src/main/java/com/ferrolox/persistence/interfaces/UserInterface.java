package com.ferrolox.persistence.interfaces;

import com.ferrolox.domain.User;

import java.util.Optional;

public interface UserInterface {

	Optional<User> findById(int id);

	Optional<User> findByUsername(String username);

	Integer save(User user);

	void update(User user);

	void delete(int id);
}