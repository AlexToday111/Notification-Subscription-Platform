package com.example.notificationplatform.infrastructure.messaging.producer;

import java.util.UUID;

public record DeliveryRequestMessage (UUID notificationId){}
