package com.example.notificationplatform.application.notification;

import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.domain.subscription.Channel;

public interface NotificationSender {

    Channel channel();

    DeliveryResult send(Notification notification);
}
