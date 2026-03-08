package com.example.notificationplatform.infrastructure.persistence.outbox;

import com.example.notificationplatform.domain.outbox.OutboxMessage;
import com.example.notificationplatform.domain.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, UUID> {
    List<OutboxMessage> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    long countByStatus(OutboxStatus status);

    @Query("select m from OutboxMessage m where m.status = 'FAILED' and m.retryCount < 10 order by m.createdAt asc")
    List<OutboxMessage> findRetryableFailures();
}
