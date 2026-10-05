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
}