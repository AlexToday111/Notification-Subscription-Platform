# Notification Subscription Platform

## Project overview
Event-driven platform that accepts application events, persists them, and delivers notifications to subscribed users via multiple channels (email, webhook, telegram). The API is intentionally thin, while business logic lives in services and message consumers.

## Architecture (event-driven)
The system is split into layers:
- api: HTTP controllers, DTOs, and error handling
- application: use-cases and orchestration
- domain: entities and domain rules
- infrastructure: persistence, messaging, security, metrics

Key infrastructure components are PostgreSQL (data), RabbitMQ (event bus + delivery queues), and Micrometer/Prometheus/Grafana (metrics). Zipkin is wired for tracing.

## Event flow (ASCII)
```
Client
  | POST /api/events
  v
EventService -> PostgreSQL (events)
  |
  | publish event.occurred
  v
RabbitMQ
  |
  v
EventConsumer -> NotificationGeneratorService -> PostgreSQL (notifications)
  |
  | enqueue delivery request
  v
RabbitMQ (delivery queue)
  |
  v
DeliveryConsumer -> NotificationDeliveryService -> Channel sender (email/webhook/telegram)
```

## Tech stack
- Java 17, Spring Boot 3
- Spring Data JPA, Flyway
- PostgreSQL 16
- RabbitMQ 3
- Micrometer + Prometheus + Grafana
- Zipkin (tracing)
- Spring Security + JWT

## How to run (docker-compose)
1) Build and start services:
```
docker-compose up --build
```
2) App is available at `http://localhost:8080`
3) RabbitMQ management UI: `http://localhost:15672` (guest/guest)
4) Prometheus: `http://localhost:9090`
5) Grafana: `http://localhost:3000`
6) Zipkin: `http://localhost:9411`

Auth endpoint (demo credentials):
- `POST /auth/login` with `{"username":"user","password":"user"}` or `{"username":"admin","password":"admin"}`

## Observability
- Health: `GET /actuator/health`
- Metrics: `GET /actuator/prometheus`
- Delivery timing: `delivery.duration` timer
- Custom counters: sent, retry, failed

## Future improvements
- Add Testcontainers for full integration tests with Postgres/RabbitMQ
- Add idempotency for event ingestion and delivery
- Exponential backoff strategy for retries
- Real channel integrations (SMTP, Telegram API, Webhooks with retries)
- Role-based access per resource and audit logging
