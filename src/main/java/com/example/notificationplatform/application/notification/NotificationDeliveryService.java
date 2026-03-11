package com.example.notificationplatform.application.notification;

import com.example.notificationplatform.infrastructure.config.RabbitConfig;
import com.example.notificationplatform.infrastructure.metrics.NotificationMetrics;
import com.example.notificationplatform.infrastructure.messaging.producer.DeliveryRequestMessage;
import com.example.notificationplatform.infrastructure.messaging.producer.NotificationFailedMessage;
import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.domain.notification.DeliveryAttempt;
import com.example.notificationplatform.infrastructure.persistence.notification.NotificationRepository;
import com.example.notificationplatform.infrastructure.persistence.notification.DeliveryAttemptRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private static final int MAX_RETRIES = 5;

    private final NotificationRepository notificationRepository;
    private final DeliveryAttemptRepository attemptRepository;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationSenderRegistry senderRegistry;

    private final NotificationMetrics metrics;
    private final MeterRegistry meterRegistry;

    @Transactional
    public void deliver(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        if (n.isTerminal()) {
            return;
        }

        n.markSending();
        DeliveryAttempt attempt = attemptRepository.save(DeliveryAttempt.started(n, n.getRetryCount() + 1));

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            DeliveryResult result = senderRegistry
                    .get(n.getChannel())
                    .send(n);

            attempt.markSucceeded(result.responseStatus(), result.diagnostic());
            n.markSent();
            metrics.incSent();
            log.info("Delivery attempt succeeded notificationId={} deliveryId={} channel={} attempt={}",
                    n.getId(), n.getId(), n.getChannel(), attempt.getAttemptNumber());
        } catch (Exception e) {
            DeliveryFailure failure = DeliveryFailure.from(e);
            attempt.markFailed(failure.code(), failure.message(), failure.responseStatus(), failure.diagnostic(), failure.retryable());
            if (failure.retryable() && n.getRetryCount() + 1 < MAX_RETRIES) {
                int nextAttempt = n.getRetryCount() + 1;
                Duration delay = backoff(nextAttempt);
                n.scheduleRetry(failure.message(), Instant.now().plus(delay));
                metrics.incRetry();
                rabbitTemplate.convertAndSend(
                        "",
                        RabbitConfig.DELIVERY_RETRY_QUEUE,
                        new DeliveryRequestMessage(n.getId(), n.getCorrelationId()),
                        message -> {
                            message.getMessageProperties().setExpiration(String.valueOf(delay.toMillis()));
                            message.getMessageProperties().setHeader("correlationId", n.getCorrelationId());
                            return message;
                        }
                );
                log.warn("Delivery retry scheduled notificationId={} channel={} attempt={} delayMs={} reason={}",
                        n.getId(), n.getChannel(), nextAttempt, delay.toMillis(), failure.message());
            } else {
                if (failure.retryable()) {
                    n.incrementRetry(failure.message());
                    n.markDeadLettered(failure.message());
                    metrics.incDeadLetter();
                } else {
                    n.markFailed(failure.message());
                    metrics.incFailed();
                }

                rabbitTemplate.convertAndSend(
                        RabbitConfig.DELIVERY_DLQ_QUEUE,
                        NotificationFailedMessage.of(n.getId(), failure.message(), n.getRetryCount())
                );

                log.error("Notification {} moved to terminal delivery state {} after {} retries. Reason={}",
                        n.getId(), n.getStatus(), n.getRetryCount(), failure.message());
            }
        } finally {
            sample.stop(meterRegistry.timer("delivery.latency"));
        }
    }

    @Transactional
    public Notification manualRetry(UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        n.resetForManualRetry();
        rabbitTemplate.convertAndSend(RabbitConfig.DELIVERY_QUEUE, new DeliveryRequestMessage(n.getId(), n.getCorrelationId()));
        log.info("Manual delivery retry requested notificationId={} channel={}", n.getId(), n.getChannel());
        return n;
    }

    private Duration backoff(int attemptNumber) {
        long seconds = Math.min(300, (long) Math.pow(2, Math.max(0, attemptNumber - 1)) * 5);
        return Duration.ofSeconds(seconds);
    }

    private record DeliveryFailure(String code, String message, Integer responseStatus, String diagnostic, boolean retryable) {
        static DeliveryFailure from(Exception e) {
            if (e instanceof DeliveryException deliveryException) {
                return new DeliveryFailure(
                        deliveryException.code(),
                        safe(deliveryException.getMessage(), e.getClass().getSimpleName()),
                        deliveryException.responseStatus(),
                        deliveryException.diagnostic(),
                        deliveryException.retryable()
                );
            }
            return new DeliveryFailure(e.getClass().getSimpleName(), safe(e.getMessage(), e.getClass().getSimpleName()), null, null, true);
        }

        private static String safe(String value, String fallback) {
            return value == null || value.isBlank() ? fallback : value;
        }
    }
}
