# Observability

Use metrics for trends, traces for request/message paths, and logs for incident detail.

## Metrics

- `events.accepted.total`
- `events.duplicate.total`
- `notifications.created.total`
- `deliveries.sent.total`
- `deliveries.failed.total`
- `deliveries.retry.total`
- `deliveries.deadletter.total`
- `delivery.latency`
- `webhook.delivery.status`
- `outbox.pending.count`
- `subscriptions.matched.total`
- `rule.evaluation.duration`

## Grafana

Import `infra/grafana/notification-platform-dashboard.json` or wire it into provisioning. Watch retry/dead-letter spikes and `outbox.pending.count`.

## Tracing

Spring AMQP and RabbitTemplate observation are enabled. Zipkin receives traces at `http://zipkin:9411/api/v2/spans`.

## Reading Signals

- DLQ spike: channel outage, bad credentials, or invalid destinations.
- Outbox pending growth: RabbitMQ unreachable or publisher failures.
- Duplicate event growth: producer retry storm or bad idempotency key reuse.
