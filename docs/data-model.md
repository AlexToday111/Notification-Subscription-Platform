# Data Model

```mermaid
erDiagram
    USERS ||--o{ SUBSCRIPTIONS : owns
    USERS ||--o{ NOTIFICATIONS : receives
    USERS ||--|| NOTIFICATION_PREFERENCES : configures
    EVENTS ||--o{ NOTIFICATIONS : creates
    SUBSCRIPTIONS ||--o{ NOTIFICATIONS : matches
    NOTIFICATIONS ||--o{ DELIVERY_ATTEMPTS : records
    NOTIFICATIONS ||--o| NOTIFICATION_DIGEST_ITEMS : may_queue
    EVENTS ||--o| INCOMING_EVENTS : originated_from
    OUTBOX_MESSAGES }o--|| EVENTS : publishes
    AUDIT_LOGS }o--|| USERS : actor
```

Main tables:

- `incoming_events`: producer idempotency registry.
- `events`: normalized internal event record.
- `subscriptions`: event type, channel, destination, optional `condition_json`.
- `notifications`: delivery state and user-facing history.
- `delivery_attempts`: per-attempt channel diagnostics.
- `outbox_messages`: transactional publication queue.
- `notification_preferences`: channel, quiet hours, digest settings.
- `notification_digest_items`: queued digest records.
- `audit_logs`: administrative and security-sensitive events.
