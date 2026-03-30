# Operations

Start the local stack:

```bash
cp .env.example .env
docker compose up --build
```

## Services

- PostgreSQL: `localhost:5432`
- RabbitMQ AMQP: `localhost:5672`
- RabbitMQ UI: `http://localhost:15672`
- App: `http://localhost:8080`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`
- Zipkin: `http://localhost:9411`
- Mailpit: `http://localhost:8025`
- Elasticsearch: `http://localhost:9200`
- Kibana: `http://localhost:5601`

## Queues

- `events.queue`
- `delivery`
- `delivery.retry`
- `delivery.dlq`

## Troubleshooting

- If events are accepted but no notifications appear, inspect `outbox.pending.count` and RabbitMQ.
- If deliveries retry, inspect `/api/v1/deliveries/{id}/attempts`.
- If logs are missing in Kibana, check the app log file volume and Logstash logs.
- If email does not appear, open Mailpit and confirm `SPRING_MAIL_HOST=mailpit`.
