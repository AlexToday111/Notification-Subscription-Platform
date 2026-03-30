# Channels

## Email

Email uses Spring Mail and SMTP configuration from environment variables. Locally, Docker Compose runs Mailpit:

- SMTP: `localhost:1025`
- UI: `http://localhost:8025`

The adapter sets subject and body and is suitable for local demos without sending real email.

## Telegram

Telegram uses the Bot API endpoint:

```text
POST /bot{token}/sendMessage
```

`APP_TELEGRAM_BOT_TOKEN` is read from the environment. 4xx errors are treated as permanent; 5xx and network failures are retryable.

## Webhook

Webhook delivery sends an HTTP POST to the subscription destination. Headers:

- `X-Notification-Timestamp`
- `X-Notification-Signature`
- `X-Correlation-Id`

The signature uses HMAC-SHA256:

```text
sha256=<hex digest>
```

5xx and network failures are retried. 4xx responses are treated as permanent because the endpoint rejected the request.
