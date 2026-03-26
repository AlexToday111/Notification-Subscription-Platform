package com.example.notificationplatform.infrastructure.testcontainers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers(disabledWithoutDocker = true)
@Disabled("Run manually with Docker to smoke-check PostgreSQL and RabbitMQ Testcontainers wiring.")
class MessagingInfrastructureTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("notification")
            .withUsername("notification")
            .withPassword("notification");

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management");

    @Test
    void startsPostgresAndRabbitMq() {
        assertNotNull(postgres.getJdbcUrl());
        assertNotNull(rabbit.getAmqpUrl());
    }
}
