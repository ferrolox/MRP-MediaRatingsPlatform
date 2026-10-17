package com.ferrolox.configuration;

import com.ferrolox.foundation.exceptions.*;
import com.ferrolox.presentation.controllers.*;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class ServerConfiguration {
	public static Javalin create(HomeController homeController, UserController userController) {
		return Javalin.create(config -> {
			config.routes.get("/", homeController::home);
			config.routes.get("/user/login", userController::login);
			config.routes.get("/user/register", userController::register);

			config.routes.exception(DatabaseException.class, (exception, context) -> {
				exception.printStackTrace();
                context.status(HttpStatus.INTERNAL_SERVER_ERROR).json("Internal server error");
			});

			config.routes.exception(InvalidCredentialsException.class, (exception, context) -> context.status(HttpStatus.BAD_REQUEST).json(exception.getMessage()));
			config.routes.exception(ConflictingCredentialsException.class, (exception, context) -> context.status(HttpStatus.CONFLICT).json(exception.getMessage()));
			config.routes.exception(WeakCredentialsException.class, (exception, context) -> context.status(HttpStatus.UNAUTHORIZED).json(exception.getMessage()));
		});
	}
}