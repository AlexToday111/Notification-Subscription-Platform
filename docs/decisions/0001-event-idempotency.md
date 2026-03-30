# 0001 Event Idempotency

External producers can retry requests after timeouts. Without an idempotency registry, the platform could create duplicate notifications.

Decision: store every accepted event in `incoming_events` and enforce uniqueness on `producer + external_event_id`.

Result: duplicate submissions return a predictable response, do not create another app event, and increment duplicate metrics.
