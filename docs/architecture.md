# Architecture

Notification Subscription Platform is split into API, application, domain, and infrastructure layers.

## Components

- Spring Boot API receives external events, subscriptions, preferences, and operator commands.
- PostgreSQL stores incoming event registry, app events, subscriptions, notifications, delivery attempts, outbox messages, preferences, digest items, and audit logs.
- RabbitMQ moves event and delivery work out of HTTP requests.
- Transactional outbox closes the gap between database commits and RabbitMQ publication.
- Channel adapters deliver Email, Telegram, and signed Webhook notifications.
- Prometheus/Grafana, Zipkin, and ELK provide metrics, traces, and logs.

```mermaid
flowchart LR
    Producer[External producer] --> API[Spring Boot API]
    API --> IE[(incoming_events)]
    API --> EV[(events)]
    API --> OB[(outbox_messages)]
    OB --> MQ[(RabbitMQ)]
    MQ --> Matcher[Rule matcher]
    Matcher --> N[(notifications)]
    N --> DA[(delivery_attempts)]
    DA --> Email[SMTP]
    DA --> Telegram[Telegram]
    DA --> Webhook[Webhook]
    API --> Audit[(audit_logs)]
```

## RabbitMQ Flow

- `app.events` exchange routes `event.occurred` to `events.queue`.
- `delivery` queue contains delivery jobs.
- `delivery.retry` delays retries by message expiration and dead-letters back to `delivery`.
- `delivery.dlq` stores terminal failure messages for operator inspection.

## Centralized Logging Flow

```mermaid
flowchart LR
    App[Notification Platform JSON logs] --> Logstash[Logstash]
    Logstash --> Elasticsearch[Elasticsearch]
    Elasticsearch --> Kibana[Kibana]
```
