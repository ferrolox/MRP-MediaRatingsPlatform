package com.ferrolox.persistence.queryObjects;

import com.ferrolox.domain.MediaType;

public record MediaEntryFilter(String title,
							   String genre,
							   MediaType type,
							   Integer releaseYear,
							   Integer minimumAge,
							   Double minimumRating,
							   MediaEntrySort sort) {}