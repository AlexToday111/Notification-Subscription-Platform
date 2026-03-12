# Обзор архитектуры

## Архитектурный стиль
Приложение реализовано как модульный монолит на Spring Boot с event-driven потоком обработки. HTTP-слой принимает запросы, сохраняет первичные данные и инициирует асинхронные действия через RabbitMQ.

## Слои
- `src/main/java/com/example/notificationplatform/api`:
  REST-контроллеры, request/response DTO, мапперы и глобальная обработка ошибок.
- `src/main/java/com/example/notificationplatform/application`:
  сервисы уровня use case, orchestration и прикладные команды.
- `src/main/java/com/example/notificationplatform/domain`:
  сущности `AppEvent`, `Subscription`, `Notification`, `User` и перечисления `Channel`, `EventType`, `NotificationStatus`.
- `src/main/java/com/example/notificationplatform/infrastructure`:
  конфигурация RabbitMQ, persistence-репозитории, security, metrics, producer/consumer-компоненты и senders каналов доставки.

## Основные компоненты
- `EventService`: сохраняет событие и публикует сообщение `event.occurred`.
- `EventConsumer`: получает сообщение из `events.queue`.
- `NotificationGeneratorService`: ищет активные подписки по типу события и создаёт уведомления.
- `NotificationDeliveryService`: пытается доставить уведомление, ведёт retry и пишет метрики.
- `SubscriptionService`: создаёт и валидирует подписки.
- `JwtService`: генерирует и валидирует JWT для демо-аутентификации.

## Важные детали реализации
- В проекте нет отдельного внешнего auth-provider: логин реализован через демо-эндпоинт `/auth/login`.
- Каналы доставки выбираются через `NotificationSenderRegistry`.
- Для очередей и RabbitTemplate включены observation hooks, чтобы метрики и tracing проходили через Micrometer.
