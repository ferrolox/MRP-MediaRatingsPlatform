package com.ferrolox.presentation.controllers;

import com.ferrolox.domain.MediaEntry;
import com.ferrolox.domain.MediaType;
import com.ferrolox.foundation.exceptions.*;
import com.ferrolox.persistence.queryObjects.MediaEntryFilter;
import com.ferrolox.persistence.queryObjects.MediaEntrySort;
import com.ferrolox.presentation.dataTransferObjects.MediaEntryRequest;
import com.ferrolox.service.MediaEntryService;

import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

@SuppressWarnings("DataFlowIssue")
public class MediaEntryController {

	private final MediaEntryService mediaEntryService;

	public MediaEntryController(MediaEntryService mediaEntryService) {
		this.mediaEntryService = mediaEntryService;
	}

	public void findById(Context context) throws DatabaseException, InvalidParametersException, InexistentResourceException {
		int id = Integer.parseInt(context.pathParam("id"));

		MediaEntry mediaEntry = mediaEntryService.findById(id);

		context.json(mediaEntry);
	}

	public void findAll(Context context) throws DatabaseException, InvalidParametersException {
		MediaEntryFilter filter = new MediaEntryFilter(
			context.queryParam("title"),
			context.queryParam("genre"),
			context.queryParam("type") == null ? null : context.queryParamAsClass("type", MediaType.class).get(),
			context.queryParam("releaseYear") == null ? null : context.queryParamAsClass("releaseYear", Integer.class).get(),
			context.queryParam("minimumAge") == null ? null : context.queryParamAsClass("minimumAge", Integer.class).get(),
			context.queryParam("minimumRating") == null ? null : context.queryParamAsClass("minimumRating", Double.class).get(),
			context.queryParam("sort") == null ? null : context.queryParamAsClass("sort", MediaEntrySort.class).get()
		);

		List<MediaEntry> mediaEntries = mediaEntryService.findAll(filter);

		context.json(mediaEntries);
	}

	public void save(Context context) throws DatabaseException, InvalidParametersException, InexistentResourceException {
		int userId = context.attribute("authenticatedUserId");

		MediaEntryRequest mediaEntryRequest = context.bodyAsClass(MediaEntryRequest.class);

		int id = mediaEntryService.save(userId, mediaEntryRequest);

		context.header("Location", "/media/" + id);
		context.status(201);
		context.json(Map.of("id", id));
	}

	public void update(Context context) throws DatabaseException, InvalidParametersException, InsufficientCredentialsException, InexistentResourceException {
		int userId = context.attribute("authenticatedUserId");
		int id = Integer.parseInt(context.pathParam("id"));

		MediaEntryRequest mediaEntryRequest = context.bodyAsClass(MediaEntryRequest.class);

		mediaEntryService.update(userId, id, mediaEntryRequest);

		context.status(204);
	}

	public void delete(Context context) throws DatabaseException, InvalidParametersException, InsufficientCredentialsException, InexistentResourceException {
		int userId = context.attribute("authenticatedUserId");
		int id = Integer.parseInt(context.pathParam("id"));

		mediaEntryService.delete(userId, id);

		context.status(204);
	}
}