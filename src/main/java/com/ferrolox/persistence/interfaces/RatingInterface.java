package com.ferrolox.persistence.interfaces;

import com.ferrolox.domain.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingInterface {

	Optional<Rating> findById(int id);

	List<Rating> findByMediaEntryId(int mediaEntryId);

	List<Rating> findByUserId(int userId);

	int save(Rating rating);

	void update(Rating rating);

	void delete(int id);
}