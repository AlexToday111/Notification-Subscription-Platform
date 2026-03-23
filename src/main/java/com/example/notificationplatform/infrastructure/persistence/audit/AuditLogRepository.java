package com.example.notificationplatform.infrastructure.persistence.audit;

import com.example.notificationplatform.domain.audit.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
