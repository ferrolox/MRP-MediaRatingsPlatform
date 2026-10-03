package com.ferrolox.persistence.interfaces;

import com.ferrolox.domain.Genre;

import java.util.Optional;
import java.util.Set;

public interface GenreInterface {

	Optional<Genre> findById(int id);

	Optional<Genre> findByName(String name);

	Set<Genre> findAll();

	int save(Genre genre);

	void update(Genre genre);

	void delete(int id);
}