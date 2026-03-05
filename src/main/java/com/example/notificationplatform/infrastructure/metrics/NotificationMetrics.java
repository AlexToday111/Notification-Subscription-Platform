package com.example.notificationplatform.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class NotificationMetrics {
    private final Counter eventsAccepted;
    private final Counter eventsDuplicate;
    private final Counter notificationsCreated;
    public final Counter sent;
    private final Counter failed;
    private final Counter retry;
    private final Counter deadLetter;
    private final Counter subscriptionsMatched;
    private final Timer deliveryTimer;
    private final Timer ruleEvaluationTimer;

    public NotificationMetrics(MeterRegistry registry) {
        this.eventsAccepted = registry.counter("events.accepted.total");
        this.eventsDuplicate = registry.counter("events.duplicate.total");
        this.notificationsCreated = registry.counter("notifications.created.total");
        this.sent = registry.counter("deliveries.sent.total");
        this.failed = registry.counter("deliveries.failed.total");
        this.retry = registry.counter("deliveries.retry.total");
        this.deadLetter = registry.counter("deliveries.deadletter.total");
        this.subscriptionsMatched = registry.counter("subscriptions.matched.total");
        this.deliveryTimer = registry.timer("delivery.latency");
        this.ruleEvaluationTimer = registry.timer("rule.evaluation.duration");
    }

    public void incAcceptedEvent() {
        eventsAccepted.increment();
    }

    public void incDuplicateEvent() {
        eventsDuplicate.increment();
    }

    public void incNotificationCreated() {
        notificationsCreated.increment();
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

    public void incDeadLetter() {
        deadLetter.increment();
    }

    public void incSubscriptionMatched() {
        subscriptionsMatched.increment();
    }

    public <T> T recordDelivery(java.util.concurrent.Callable<T> action) throws Exception {
        return deliveryTimer.recordCallable(action);
    }

    public void recordDelivery(Runnable action) {
        deliveryTimer.record(action);
    }

    public <T> T recordRuleEvaluation(java.util.concurrent.Callable<T> action) throws Exception {
        return ruleEvaluationTimer.recordCallable(action);
    }
}
