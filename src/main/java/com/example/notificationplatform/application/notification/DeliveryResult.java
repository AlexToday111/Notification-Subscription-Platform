package com.example.notificationplatform.application.notification;

public record DeliveryResult(
        Integer responseStatus,
        String diagnostic
) {
    public static DeliveryResult ok() {
        return new DeliveryResult(null, "accepted");
    }
}
