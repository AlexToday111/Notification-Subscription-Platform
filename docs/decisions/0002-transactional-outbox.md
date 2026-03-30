# 0002 Transactional Outbox

Publishing to RabbitMQ inside the same use case as a database write can lose messages if the database commits and the broker publish fails.

Decision: write an `outbox_messages` row in the same transaction as event/notification changes. A scheduled publisher sends pending rows to RabbitMQ and marks them published after success.

Outbox retry handles broker publication failures. Delivery retry handles downstream channel failures after the delivery job has been published.
