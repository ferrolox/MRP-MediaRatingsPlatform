package com.ferrolox.persistence.interfaces;

import com.ferrolox.domain.*;
import com.ferrolox.persistence.queryObjects.MediaEntryFilter;

import java.util.List;
import java.util.Optional;

public interface MediaEntryInterface {

	Optional<MediaEntry> findById(int id);

	List<MediaEntry> find(MediaEntryFilter filter);

	List<MediaEntry> findFavouritesByUserId(int userId);

	int save(MediaEntry mediaEntry);

	void update(MediaEntry mediaEntry);

	void delete(int id);
}