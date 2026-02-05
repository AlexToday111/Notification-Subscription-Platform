package com.example.notificationplatform.notification.service.delivery;

import com.example.notificationplatform.notification.domain.Notification;
import com.example.notificationplatform.subscription.domain.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebhookNotificationSender implements NotificationSender{

    @Override
    public Channel channel() {
        return Channel.WEBHOOK;
    }

    @Override
    public void send(Notification notification) {
        log.info(
                "[WEBHOOK] url={} payload='{}'",
                notification.getDestination(),
                notification.getContent()
        );
    }
}
