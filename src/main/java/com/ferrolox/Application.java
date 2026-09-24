package com.ferrolox;

import com.ferrolox.configuration.DatabaseConfiguration;
import com.ferrolox.configuration.ContainerConfiguration;

public class Application {

    void main() {
        ContainerConfiguration.start();

        DatabaseConfiguration database = new DatabaseConfiguration(
            ContainerConfiguration.getJdbcUrl(),
            ContainerConfiguration.getUsername(),
            ContainerConfiguration.getPassword()
        );

        database.migrate();

        // Repositories
        // Services
        // HTTP server
    }
}