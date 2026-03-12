# Эксплуатация и наблюдаемость

## Инфраструктура в docker-compose
- `db`: PostgreSQL 16
- `rabbitmq`: RabbitMQ 3 с management UI
- `app`: Spring Boot приложение
- `prometheus`: сбор метрик
- `grafana`: визуализация метрик
- `zipkin`: distributed tracing

## Конфигурация приложения
Ключевые параметры находятся в `src/main/resources/application.yml`:
- datasource PostgreSQL
- Flyway migrations
- RabbitMQ host, credentials и listener settings
- JWT secret и TTL
- management endpoints
- tracing export в Zipkin

## Очереди и exchange
- `app.events`: topic exchange
- `events.queue`: очередь получения событий
- `delivery`: очередь доставки уведомлений
- `delivery.retry`: очередь повторной попытки
- `delivery.dlq`: очередь окончательных ошибок

Технические детали:
- `acknowledge-mode: manual`
- `prefetch: 5`
- `default-requeue-rejected: false`
- observation для template и listener включён

## Метрики
Приложение публикует:
- `notifications.sent.count`
- `notifications.retry.count`
- `notifications.failed.count`
- `delivery.duration`

## Monitoring endpoints
- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`

## Что проверить после запуска
1. Доступность `http://localhost:8080/actuator/health`
2. Логин через `POST /auth/login`
3. Наличие RabbitMQ UI на `http://localhost:15672`
4. Доступность метрик Prometheus и Grafana
5. При необходимости, наличие trace'ов в Zipkin
