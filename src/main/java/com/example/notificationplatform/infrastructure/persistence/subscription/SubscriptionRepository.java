package com.example.notificationplatform.infrastructure.persistence.subscription;

import com.example.notificationplatform.domain.event.EventType;
import com.example.notificationplatform.domain.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findByUserId(UUID userId);

    List<Subscription> findByEventTypeAndActiveTrue(EventType eventType);
}