package com.example.notificationplatform.application.audit;

import com.example.notificationplatform.domain.audit.AuditLog;
import com.example.notificationplatform.infrastructure.persistence.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void record(String action, String entityType, String entityId, String details) {
        auditLogRepository.save(AuditLog.record(actor(), action, entityType, entityId, details, MDC.get("correlationId")));
    }

    private String actor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return "system";
        }
        return authentication.getName();
    }
}
