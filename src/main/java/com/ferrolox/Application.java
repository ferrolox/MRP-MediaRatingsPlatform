package com.ferrolox;

import com.ferrolox.configuration.*;
import com.ferrolox.persistence.repositories.*;
import com.ferrolox.presentation.controllers.*;
import com.ferrolox.security.*;
import com.ferrolox.service.*;

import java.io.IOException;
import java.time.Duration;

import com.github.lalyos.jfiglet.FigletFont;
import io.javalin.Javalin;

public class Application {

    @SuppressWarnings("JavaPrintToLogpoint")
    void main() throws IOException {
        ContainerConfiguration.start();

        DatabaseConfiguration database = new DatabaseConfiguration(
            ContainerConfiguration.getJdbcUrl(),
            ContainerConfiguration.getUsername(),
            ContainerConfiguration.getPassword()
        );

		IO.println();
		IO.println("\033[36m" + FigletFont.convertOneLine("MRP-Media Ratings Platform") + "\033[0m");
		IO.println("\033[1mA Java Standalone Backend for an idiomatic Media Rating Platform\033[0m"); //TODO: Add a better description
		IO.println("────────────────────────────────────────────────────────────────");
		IO.println("\033[33m[ ... ]\033[0m Starting database");

		database.migrate();

		IO.println("\033[32m[ OK ]\033[0m Database ready");
		IO.println("\033[33m[ ... ]\033[0m Starting HTTP server");

	    UserRepository userRepository = new UserRepository(database);

	    HashingService hashingService = new HashingService(System.getenv("PASSWORD_HASH_PEPPER"));
	    TokenService tokenService = new TokenService(System.getenv("JWT_SECRET"), Duration.ofDays(7));
	    UserService userService = new UserService(userRepository, hashingService, tokenService);

		HomeController homeController = new HomeController();
	    UserController userController = new UserController(userService);

		Javalin server = ServerConfiguration.create(homeController, userController);
		server.start(8080);

		IO.println("\033[32m[ OK ]\033[0m HTTP server running on port 8080");
	}
}