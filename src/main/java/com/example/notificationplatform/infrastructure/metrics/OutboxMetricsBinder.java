package com.example.notificationplatform.infrastructure.metrics;

import com.example.notificationplatform.domain.outbox.OutboxStatus;
import com.example.notificationplatform.infrastructure.persistence.outbox.OutboxMessageRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OutboxMetricsBinder {
    public OutboxMetricsBinder(MeterRegistry registry, OutboxMessageRepository repository) {
        Gauge.builder("outbox.pending.count", repository, r -> r.countByStatus(OutboxStatus.PENDING))
                .register(registry);
    }
}
