package com.example.notificationplatform.notification.service.delivery;

import com.example.notificationplatform.notification.domain.Notification;
import com.example.notificationplatform.subscription.domain.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TelegramNotificationSender implements NotificationSender{
    @Override
    public Channel channel() {
        return Channel.TELEGRAM;
    }

    @Override
    public void send(Notification notification) {
        log.info(
                "[TELEGRAM] chatId={} content='{}'",
                notification.getDestination(),
                notification.getContent()
        );
    }
}
