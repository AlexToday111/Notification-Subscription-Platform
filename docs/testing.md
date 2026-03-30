# Testing

Run the default suite:

```bash
mvn test
```

Coverage includes:

- idempotent incoming event handling
- subscription rule matching
- quiet hours and preferences validation
- delivery success and retry scheduling
- JPA repository smoke checks

`MessagingInfrastructureTest` contains PostgreSQL and RabbitMQ Testcontainers wiring and is disabled by default so the normal suite remains deterministic on machines without Docker. Remove `@Disabled` or run an equivalent local profile when Docker is available.

ELK is not started in automated tests because it is operational infrastructure rather than application logic. The repository covers JSON log configuration and validates Docker Compose config; Logstash/Kibana behavior is documented as a local smoke-check in `docs/logging.md`.
