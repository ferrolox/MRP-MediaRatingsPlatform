package com.ferrolox.configuration;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
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
				   .withHostConfig(new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(5432), new ExposedPort(5432))))
        );

    public static void start() {
        final int maximumRetries = 10;
        final long retryDelay = 5000;

        for (int attempt = 1; attempt <= maximumRetries; attempt++) {
            try {
                postgresContainer.start();
                return;
            } catch (Exception exception) {
                if (attempt == maximumRetries) {
                    throw new RuntimeException("Failed to start PostgreSQL Container after " + maximumRetries + " attempts.", exception);
                }

                try { Thread.sleep(retryDelay); }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("PostgreSQL startup interrupted.", interruptedException);
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