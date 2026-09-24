package com.ferrolox.configuration;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class ContainerConfiguration {

    private static final PostgreSQLContainer postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"))
        .withDatabaseName("database")
        .withUsername("username")
        .withPassword("password")
        .withReuse(true)
        .withCreateContainerCmdModifier(command ->
            command.withName("BIF3E-MediaRatingsPlatform-Postgres")
        );

    public static void start() {
        final int maxRetries = 10;
        final long retryDelay = 5000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                postgresContainer.start();
                return;
            } catch (Exception exception) {
                if (attempt == maxRetries) {
                    throw new RuntimeException(
                            "Failed to start PostgreSQL after " + maxRetries + " attempts.",
                            exception
                    );
                }

                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(
                            "PostgreSQL startup interrupted.",
                            interruptedException
                    );
                }
            }
        }
    }

    public static String getJdbcUrl() {
        return postgresContainer.getJdbcUrl();
    }

    public static String getUsername() {
        return postgresContainer.getUsername();
    }

    public static String getPassword() {
        return postgresContainer.getPassword();
    }
}