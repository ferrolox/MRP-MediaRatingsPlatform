package com.ferrolox.persistence.interfaces;

import com.ferrolox.domain.*;
import com.ferrolox.foundation.exceptions.DatabaseException;
import com.ferrolox.foundation.exceptions.InexistentResourceException;
import com.ferrolox.persistence.queryObjects.MediaEntryFilter;

import java.util.List;
import java.util.Optional;

public interface MediaEntryInterface {

	List<MediaEntry> find(MediaEntryFilter filter) throws DatabaseException;

	Optional<MediaEntry> findById(int id) throws DatabaseException;

	List<MediaEntry> findFavouritesByUserId(int userId) throws DatabaseException;

	int save(MediaEntry mediaEntry) throws DatabaseException;

	void update(MediaEntry mediaEntry) throws DatabaseException, InexistentResourceException;

	void deleteById(int id) throws DatabaseException, InexistentResourceException;
}