package com.example.notificationplatform.notification.service.delivery;

import com.example.notificationplatform.subscription.domain.Channel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationSenderRegistry {

    private final List<NotificationSender> senders;
    private Map<Channel, NotificationSender> byChannel;

    @PostConstruct
    void init(){
        this.byChannel = new EnumMap<>(Channel.class);
        for (NotificationSender sender : senders){
            byChannel.put(sender.channel(), sender);
        }
    }
    public NotificationSender get(Channel channel){
        NotificationSender sender = byChannel.get(channel);
        if (sender == null) {
            throw new IllegalStateException("No sender for channel: " + channel);
        }
        return sender;
    }
}
