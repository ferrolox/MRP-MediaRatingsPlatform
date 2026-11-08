package com.ferrolox.presentation.dataTransferObjects;

import com.ferrolox.domain.Genre;
import com.ferrolox.domain.MediaType;

import java.util.Set;

public record MediaEntryRequest(
	String title,
	String description,
	MediaType type,
	Set<Genre> genres,
	int releaseYear,
	int minimumAge
) {}