# Модель данных

## Таблицы

### `users`
- `id`: UUID, primary key
- `email`: email пользователя

### `events`
- `id`: UUID, primary key
- `type`: тип события
- `payload`: JSONB payload
- `created_at`: время создания

### `subscriptions`
- `id`: UUID, primary key
- `user_id`: ссылка на пользователя
- `channel`: канал уведомления
- `destination`: адрес назначения
- `event_type`: тип события, на который оформлена подписка

### `notifications`
- `id`: UUID, primary key
- `user_id`: пользователь-получатель
- `event_id`: событие-источник
- `subscription_id`: подписка, из которой создано уведомление
- `channel`: канал доставки
- `destination`: адрес назначения
- `status`: текущий статус уведомления
- `content`: текст уведомления
- `retry_count`: число попыток повтора
- `last_error`: последняя ошибка доставки
- `created_at`, `updated_at`: технические таймстемпы

## Индексы
- `ix_notifications_user_created_at`
- `ix_notifications_status`

## Доменные перечисления

### `Channel`
- `EMAIL`: валидируется как email
- `TELEGRAM`: принимает username или chat id
- `WEBHOOK`: принимает `http://` или `https://` URL

### `NotificationStatus`
- `NEW`
- `SENDING`
- `SENT`
- `RETRYING`
- `FAILED`

Терминальные статусы: `SENT`, `FAILED`.

## Бизнес-правила
- Подписка создаётся только для существующего пользователя.
- `destination` валидируется в зависимости от выбранного канала.
- Для каждого подходящего `Subscription` создаётся отдельный `Notification`.
