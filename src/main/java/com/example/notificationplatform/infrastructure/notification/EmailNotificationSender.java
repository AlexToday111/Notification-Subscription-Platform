package com.example.notificationplatform.infrastructure.notification;

import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.domain.subscription.Channel;
import com.example.notificationplatform.application.notification.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationSender implements NotificationSender{
    @Override
    public Channel channel(){
        return Channel.EMAIL;
    }
    @Override
    public void send(Notification notification){
        log.info(
                "[EMAIL] to={} content='{}'",
                notification.getDestination(),
                notification.getContent()
        );
    }
}
