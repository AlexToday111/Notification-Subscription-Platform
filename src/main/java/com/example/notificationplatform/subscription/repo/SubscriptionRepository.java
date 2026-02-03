package com.example.notificationplatform.subscription.repo;

import com.example.notificationplatform.event.domain.EventType;
import com.example.notificationplatform.subscription.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findByUserId(UUID userId);

    List<Subscription> findByEventTypeAndActiveTrue(EventType eventType);
}