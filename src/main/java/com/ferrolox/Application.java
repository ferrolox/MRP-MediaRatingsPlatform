package com.ferrolox;

import com.ferrolox.configuration.*;
import com.ferrolox.presentation.*;

import io.javalin.Javalin;

public class Application {

    void main() {
        ContainerConfiguration.start();

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(
            ContainerConfiguration.getJdbcUrl(),
            ContainerConfiguration.getUsername(),
            ContainerConfiguration.getPassword()
        );

        databaseConfiguration.migrate();

		Javalin server = ServerConfiguration.create(new HomeController());

		server.start(8080);

        // Repositories
        // Services
    }
}