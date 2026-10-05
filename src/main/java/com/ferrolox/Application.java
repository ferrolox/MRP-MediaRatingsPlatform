package com.ferrolox;

import com.ferrolox.configuration.*;

import com.ferrolox.presentation.controllers.HomeController;
import com.github.lalyos.jfiglet.FigletFont;
import io.javalin.Javalin;

import java.io.IOException;

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

		Javalin server = ServerConfiguration.create(new HomeController());
		server.start(8080);

		IO.println("\033[32m[ OK ]\033[0m HTTP server running on port 8080");

        // Repositories
        // Services
    }
}