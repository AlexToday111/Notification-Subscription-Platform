package com.example.notificationplatform.messaging.producer;

import java.util.UUID;

public record DeliveryRequestMessage (UUID notificationId){}
