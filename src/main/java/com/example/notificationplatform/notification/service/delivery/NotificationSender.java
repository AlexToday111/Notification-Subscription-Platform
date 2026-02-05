package com.example.notificationplatform.notification.service.delivery;

import com.example.notificationplatform.notification.domain.Notification;
import com.example.notificationplatform.subscription.domain.Channel;

public interface NotificationSender {

    Channel channel();

    void send(Notification notification);
}
