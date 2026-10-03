package com.ferrolox.domain;

import java.util.Set;

public class MediaEntry {
	private User author;
	private String title;
	private String description;
	private MediaType type;
	private Set<Genre> genres;
	private int releaseYear;
	private int minimumAge;
	private Set<User> favourites;
}