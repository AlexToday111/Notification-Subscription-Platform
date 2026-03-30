# Event Processing

`POST /api/events` accepts an event with `externalEventId`, `producer`, `type`, `source`, and JSON `payload`.

The pair `producer + externalEventId` is unique in `incoming_events`. If the same event is submitted again, the service returns a duplicate response, does not create another app event or notification, and increments `events.duplicate.total`.

```mermaid
sequenceDiagram
    participant Producer
    participant API
    participant DB as PostgreSQL
    participant Outbox
    participant MQ as RabbitMQ
    participant Worker

    Producer->>API: POST /api/events
    API->>DB: insert incoming_events
    API->>DB: insert events
    API->>Outbox: insert EVENT_OCCURRED
    Outbox->>MQ: publish event.occurred
    MQ->>Worker: consume event
    Worker->>DB: match subscriptions
    Worker->>DB: create notifications
    Worker->>Outbox: insert DELIVERY_REQUESTED
```

Rule matching only runs after event deduplication. Invalid subscription rules evaluate to `false`, so malformed conditions do not break event processing.
