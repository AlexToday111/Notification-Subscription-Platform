package com.example.notificationplatform.api.delivery;

import com.example.notificationplatform.api.delivery.dto.DeliveryAttemptResponse;
import com.example.notificationplatform.api.delivery.dto.DeliveryResponse;
import com.example.notificationplatform.application.audit.AuditService;
import com.example.notificationplatform.application.notification.NotificationDeliveryService;
import com.example.notificationplatform.application.exception.NotFoundException;
import com.example.notificationplatform.domain.notification.DeliveryAttempt;
import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.domain.notification.NotificationStatus;
import com.example.notificationplatform.infrastructure.persistence.notification.DeliveryAttemptRepository;
import com.example.notificationplatform.infrastructure.persistence.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DeliveryController {

    private final NotificationRepository notificationRepository;
    private final DeliveryAttemptRepository attemptRepository;
    private final NotificationDeliveryService deliveryService;
    private final AuditService auditService;

    @GetMapping("/deliveries/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public DeliveryResponse get(@PathVariable UUID id) {
        return toDelivery(notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Delivery not found: " + id)));
    }

    @GetMapping("/deliveries/{id}/attempts")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public List<DeliveryAttemptResponse> attempts(@PathVariable UUID id) {
        return attemptRepository.findByNotification_IdOrderByStartedAtAsc(id).stream()
                .map(this::toAttempt)
                .toList();
    }

    @PostMapping("/deliveries/{id}/retry")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public DeliveryResponse retry(@PathVariable UUID id) {
        Notification notification = deliveryService.manualRetry(id);
        auditService.record("DELIVERY_MANUAL_RETRY", "Notification", id.toString(), "Manual retry requested");
        return toDelivery(notification);
    }

    @GetMapping("/admin/dead-letter")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public List<DeliveryResponse> deadLetter() {
        return notificationRepository.findByStatusOrderByUpdatedAtDesc(NotificationStatus.DEAD_LETTERED).stream()
                .map(this::toDelivery)
                .toList();
    }

    private DeliveryResponse toDelivery(Notification n) {
        return new DeliveryResponse(
                n.getId(),
                n.getUser().getId(),
                n.getEvent().getId(),
                n.getChannel(),
                n.getDestination(),
                n.getStatus(),
                n.getRetryCount(),
                n.getNextRetryAt(),
                n.getErrorMessage(),
                n.getCorrelationId(),
                n.getCreatedAt(),
                n.getUpdatedAt()
        );
    }

    private DeliveryAttemptResponse toAttempt(DeliveryAttempt a) {
        return new DeliveryAttemptResponse(
                a.getId(),
                a.getNotification().getId(),
                a.getAttemptNumber(),
                a.getChannel(),
                a.getStatus(),
                a.getStartedAt(),
                a.getCompletedAt(),
                a.getErrorCode(),
                a.getErrorMessage(),
                a.getResponseStatus(),
                a.getResponseDiagnostic(),
                a.isRetryable(),
                a.getCorrelationId()
        );
    }
}
