package com.example.notificationplatform.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class NotificationMetrics {
    public final Counter sent;
    private final Counter failed;
    private final Counter retry;
    private final Timer deliveryTimer;

    public NotificationMetrics(MeterRegistry registry) {
        this.sent = registry.counter("notifications.sent.count");
        this.failed = registry.counter("notifications.failed.count");
        this.retry = registry.counter("notifications.retry.count");
        this.deliveryTimer = registry.timer("delivery.duration");
    }

    public void incSent() {
        sent.increment();
    }

    public void incFailed() {
        failed.increment();
    }

    public void incRetry() {
        retry.increment();
    }

    public <T> T recordDelivery(java.util.concurrent.Callable<T> action) throws Exception {
        return deliveryTimer.recordCallable(action);
    }

    public void recordDelivery(Runnable action) {
        deliveryTimer.record(action);
    }
}
