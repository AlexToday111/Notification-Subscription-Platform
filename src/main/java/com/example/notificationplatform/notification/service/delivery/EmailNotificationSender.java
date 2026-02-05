package com.example.notificationplatform.notification.service.delivery;

import com.example.notificationplatform.notification.domain.Notification;
import com.example.notificationplatform.subscription.domain.Channel;
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
