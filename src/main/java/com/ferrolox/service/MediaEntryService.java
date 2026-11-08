package com.ferrolox.service;

import com.ferrolox.domain.Genre;
import com.ferrolox.domain.MediaEntry;
import com.ferrolox.domain.User;
import com.ferrolox.foundation.exceptions.*;
import com.ferrolox.persistence.queryObjects.MediaEntryFilter;
import com.ferrolox.persistence.repositories.MediaEntryRepository;
import com.ferrolox.presentation.dataTransferObjects.MediaEntryRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MediaEntryService {

	private final MediaEntryRepository mediaEntryRepository;
	private final UserService userService;

	public MediaEntryService(MediaEntryRepository mediaEntryRepository, UserService userService) {
		this.mediaEntryRepository = mediaEntryRepository;
		this.userService = userService;
	}

	public List<MediaEntry> findAll(MediaEntryFilter filter) throws DatabaseException, InvalidParametersException {
		validateFilter(filter);

		return mediaEntryRepository.find(filter);
	}

	public MediaEntry findById(int id) throws DatabaseException, InvalidParametersException, InexistentResourceException {
		if (id <= 0) throw new InvalidParametersException("Media Entry ID must be positive.");

		return mediaEntryRepository.findById(id)
		                           .orElseThrow(() -> new InexistentResourceException("Media entry with id " + id + " does not exist."));
	}

	public int save(int userId, MediaEntryRequest mediaEntryRequest) throws DatabaseException, InvalidParametersException, InexistentResourceException {
		User author = userService.findById(userId);

		MediaEntry mediaEntry = new MediaEntry(
			0,
			author,
			mediaEntryRequest.title(),
			mediaEntryRequest.description(),
			mediaEntryRequest.type(),
			mediaEntryRequest.genres(),
			mediaEntryRequest.releaseYear(),
			mediaEntryRequest.minimumAge(),
			null
		);

		validateMediaEntry(mediaEntry);
		validateGenres(mediaEntry.getGenres());

		return mediaEntryRepository.save(mediaEntry);
	}

	public void update(int userId, int id, MediaEntryRequest mediaEntryRequest) throws DatabaseException, InvalidParametersException, InexistentResourceException, InsufficientCredentialsException {
		MediaEntry existing = findById(id);

		if (existing.getAuthor().getId() != userId) throw new InsufficientCredentialsException("You are not allowed to modify this Media Entry.");

		MediaEntry mediaEntry = new MediaEntry(
			id,
			existing.getAuthor(),
			mediaEntryRequest.title(),
			mediaEntryRequest.description(),
			mediaEntryRequest.type(),
			mediaEntryRequest.genres(),
			mediaEntryRequest.releaseYear(),
			mediaEntryRequest.minimumAge(),
			existing.getFavourites()
		);

		validateMediaEntry(mediaEntry);
		validateGenres(mediaEntry.getGenres());

		mediaEntryRepository.update(mediaEntry);
	}

	public void delete(int userId, int id) throws DatabaseException, InexistentResourceException, InsufficientCredentialsException, InvalidParametersException {
		MediaEntry existing = findById(id);

		if (existing.getAuthor().getId() != userId) throw new InsufficientCredentialsException("You are not allowed to delete this Media Entry.");

		mediaEntryRepository.deleteById(id);
	}

	private void validateMediaEntry(MediaEntry mediaEntry) throws InvalidParametersException {
		if (mediaEntry == null) throw new InvalidParametersException("Media Entry must not be null.");
		if (mediaEntry.getTitle() == null || mediaEntry.getTitle().isBlank()) throw new InvalidParametersException("Title must not be empty.");
		if (mediaEntry.getTitle().length() > 255) throw new InvalidParametersException("Title must not exceed 255 characters.");
		if (mediaEntry.getDescription() == null) throw new InvalidParametersException("Description must not be null.");
		if (mediaEntry.getType() == null) throw new InvalidParametersException("Media type must not be null.");
		if (mediaEntry.getReleaseYear() <= 0) throw new InvalidParametersException("Release year must be positive.");
		if (mediaEntry.getMinimumAge() < 0) throw new InvalidParametersException("Minimum age must not be negative.");
		if (mediaEntry.getGenres() == null) throw new InvalidParametersException("Genres must not be null.");
	}

	private void validateGenres(Set<Genre> genres) throws InvalidParametersException {
		Set<Integer> genreIds = new HashSet<>();

		for (Genre genre : genres) {
			if (genre == null || genre.getId() <= 0) throw new InvalidParametersException("Invalid genre.");
			if (!genreIds.add(genre.getId())) throw new InvalidParametersException("Duplicate genre ID: " + genre.getId());
		}
	}

	private void validateFilter(MediaEntryFilter filter) throws InvalidParametersException {
		if (filter == null) return;
		if (filter.releaseYear() != null && filter.releaseYear() <= 0) throw new InvalidParametersException("Release year must be positive.");
		if (filter.minimumAge() != null && filter.minimumAge() < 0) throw new InvalidParametersException("Minimum age must not be negative.");
		if (filter.minimumRating() != null && (filter.minimumRating() < 1 || filter.minimumRating() > 5)) throw new InvalidParametersException("Minimum rating must be between 1 and 5.");
	}
}