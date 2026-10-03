package com.ferrolox.configuration;

import com.ferrolox.presentation.HomeController;
import io.javalin.Javalin;

public class ServerConfiguration {
	public static Javalin create(HomeController homeController) {
		return Javalin.create(config -> {
			config.routes.get("/", homeController::home);
		});
	}
}