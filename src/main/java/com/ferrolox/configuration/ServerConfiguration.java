package com.ferrolox.configuration;

import com.ferrolox.foundation.exceptions.*;
import com.ferrolox.presentation.controllers.*;

import com.ferrolox.security.TokenService;
import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;

public class ServerConfiguration {
	public static Javalin create(TokenService tokenService, HomeController homeController, UserController userController, MediaEntryController mediaEntryController) {
		return Javalin.create(config -> {
			config.routes.get("/", homeController::home);
			config.routes.get("/user/login", userController::login);
			config.routes.get("/user/register", userController::register);

			config.routes.get("/media", mediaEntryController::findAll);
			config.routes.get("/media/{id}", mediaEntryController::findById);
			config.routes.post("/media", mediaEntryController::save);
			config.routes.put("/media/{id}", mediaEntryController::update);
			config.routes.delete("/media/{id}", mediaEntryController::delete);

			config.routes.exception(DatabaseException.class, (exception, context) -> {
				exception.printStackTrace();
                context.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Internal server error");
			});

			config.routes.exception(InvalidParametersException.class, (exception, context) -> context.status(HttpStatus.BAD_REQUEST).json(exception.getMessage()));
			config.routes.exception(InvalidCredentialsException.class, (exception, context) -> context.status(HttpStatus.UNAUTHORIZED).json(exception.getMessage()));
			config.routes.exception(InsufficientCredentialsException.class, (exception, context) -> context.status(HttpStatus.FORBIDDEN).json(exception.getMessage()));
			config.routes.exception(InexistentResourceException.class, (exception, context) -> context.status(HttpStatus.NOT_FOUND).json(exception.getMessage()));
			config.routes.exception(ConflictingCredentialsException.class, (exception, context) -> context.status(HttpStatus.CONFLICT).json(exception.getMessage()));

			config.routes.beforeMatched(context -> {
				String routePath = context.path();

				if (context.method() == HandlerType.GET && (routePath.equals("/media") || routePath.matches("/media/\\d+"))) {
					return;
				}

				String[] publicRoutes = {"/user/login", "/user/register"};

				for (String route : publicRoutes) {
					if (routePath.equals(route)) return;
				}

				String authorization = context.header("Authorization");

				if (authorization == null || !authorization.startsWith("Bearer ")) {
					context.status(HttpStatus.UNAUTHORIZED).json("Authentication needs to be provided");
					context.skipRemainingHandlers();
					return;
				}

				try {
					String token = authorization.substring(7);
					int userId = tokenService.validate(token);

					context.attribute("authenticatedUserId", userId);
				} catch (io.jsonwebtoken.JwtException | IllegalArgumentException exception) {
					context.status(HttpStatus.UNAUTHORIZED).json("Invalid or expired token");
					context.skipRemainingHandlers();
				}
			});
		});
	}
}