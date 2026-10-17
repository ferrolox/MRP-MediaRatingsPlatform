package com.ferrolox.domain;

import java.util.Set;

public class MediaEntry {
	private int id;
	private User author;
	private String title;
	private String description;
	private MediaType type;
	private Set<Genre> genres;
	private int releaseYear;
	private int minimumAge;
	private Set<User> favourites;

	public MediaEntry(int id, User author, String title, String description, MediaType type, Set<Genre> genres, int releaseYear, int minimumAge, Set<User> favourites) {
		this.id = id;
		this.author = author;
		this.title = title;
		this.description = description;
		this.type = type;
		this.genres = genres;
		this.releaseYear = releaseYear;
		this.minimumAge = minimumAge;
		this.favourites = favourites;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MediaType getType() {
		return type;
	}

	public void setType(MediaType type) {
		this.type = type;
	}

	public Set<Genre> getGenres() {
		return genres;
	}

	public void setGenres(Set<Genre> genres) {
		this.genres = genres;
	}

	public int getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}

	public int getMinimumAge() {
		return minimumAge;
	}

	public void setMinimumAge(int minimumAge) {
		this.minimumAge = minimumAge;
	}

	public Set<User> getFavourites() {
		return favourites;
	}

	public void setFavourites(Set<User> favourites) {
		this.favourites = favourites;
	}
}