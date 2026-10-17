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

	public MediaEntry getMediaEntry() {
		return mediaEntry;
	}

	public void setMediaEntry(MediaEntry mediaEntry) {
		this.mediaEntry = mediaEntry;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Set<User> getLikes() {
		return likes;
	}

	public void setLikes(Set<User> likes) {
		this.likes = likes;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}