package com.ferrolox.domain;

import java.time.LocalDateTime;
import java.util.Set;

public class Rating {
	private User author;
	private MediaEntry mediaEntry;
	private int stars;
	private String text;
	private Set<User> likes;
	private LocalDateTime timestamp;
	private boolean hidden;
}