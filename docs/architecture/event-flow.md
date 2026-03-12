# Поток событий и доставки

## 1. Приём события
- Клиент отправляет `POST /api/events`.
- `EventController` собирает `PublishEventCommand`.
- `EventService` валидирует входные данные, сохраняет запись в таблицу `events` и публикует `EventOccurredMessage` в RabbitMQ.

## 2. Публикация в RabbitMQ
- Exchange: `app.events`
- Routing key: `event.occurred`
- Очередь потребления: `events.queue`
- Binding настроен на шаблон `event.*`

## 3. Генерация уведомлений
- `EventConsumer` читает сообщение из `events.queue`.
- `NotificationGeneratorService` определяет `EventType`, сериализует payload и выбирает активные подписки по типу события.
- Для каждой подписки создаётся отдельная запись `Notification`.

## 4. Постановка на доставку
В текущей версии README и кода логический шаг доставки подразумевает постановку уведомления в очередь `delivery`. В проекте уже есть отдельный consumer `DeliveryConsumer`, который ожидает сообщения `DeliveryRequestMessage`.

## 5. Обработка доставки
- `DeliveryConsumer` читает очередь `delivery`.
- `NotificationDeliveryService` загружает уведомление по `notificationId`.
- Для канала выбирается конкретный sender: `email`, `webhook` или `telegram`.

## 6. Retry и DLQ
- Основная очередь доставки: `delivery`
- Retry-очередь: `delivery.retry`
- DLQ: `delivery.dlq`
- TTL у `delivery.retry`: `10000` мс
- Максимум попыток доставки: `5`

Если отправка завершается ошибкой:
- пока число попыток меньше лимита, уведомление получает статус retry и повторно публикуется в `delivery.retry`
- после исчерпания лимита уведомление получает статус `FAILED`, а в `delivery.dlq` уходит `NotificationFailedMessage`

## Важная особенность текущей реализации
`NotificationGeneratorService` повторно сохраняет событие в таблицу `events` со значением `source = "rabbit"` на этапе обработки сообщения. То есть после приёма события через API и после его обработки consumer'ом в базе появляются две записи о событии с разными идентификаторами.
