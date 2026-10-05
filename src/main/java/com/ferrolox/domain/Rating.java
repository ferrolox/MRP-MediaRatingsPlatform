package com.ferrolox.domain;

import java.time.LocalDateTime;
import java.util.Set;

public class Rating {
	private int id;
	private User author;
	private MediaEntry mediaEntry;
	private int stars;
	private String text;
	private Set<User> likes;
	private LocalDateTime timestamp;
	private boolean hidden;

	public Rating(int id, User author, MediaEntry mediaEntry, int stars, String text, Set<User> likes, LocalDateTime timestamp, boolean hidden) {
		this.id = id;
		this.author = author;
		this.mediaEntry = mediaEntry;
		this.stars = stars;
		this.text = text;
		this.likes = likes;
		this.timestamp = timestamp;
		this.hidden = hidden;
	}
}