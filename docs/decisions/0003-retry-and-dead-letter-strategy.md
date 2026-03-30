# 0003 Retry And Dead Letter Strategy

Notification channels fail in different ways. Some failures are temporary, such as network timeouts or 5xx responses. Others are permanent, such as invalid channel configuration or 4xx webhook responses.

Decision: persist every attempt, retry transient failures with exponential backoff, mark permanent failures as `FAILED`, and move exhausted retryable failures to `DEAD_LETTERED`.

Operators can inspect attempt history and manually requeue failed or dead-lettered deliveries.
