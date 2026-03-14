package com.example.notificationplatform.infrastructure.notification;

import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.domain.subscription.Channel;
import com.example.notificationplatform.application.notification.DeliveryResult;
import com.example.notificationplatform.application.notification.NotificationSender;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationSender implements NotificationSender{
    private final JavaMailSender mailSender;
    private final String from;

    public EmailNotificationSender(JavaMailSender mailSender,
                                   @Value("${app.channels.email.from:no-reply@notification-platform.local}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public Channel channel(){
        return Channel.EMAIL;
    }
    @Override
    public DeliveryResult send(Notification notification){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            helper.setTo(notification.getDestination());
            helper.setSubject("Notification: " + notification.getEvent().getType().description());
            helper.setText(render(notification), false);
            mailSender.send(message);
            log.info("Email send result notificationId={} channel={} recipient={}",
                    notification.getId(), notification.getChannel(), notification.getDestination());
            return DeliveryResult.ok();
        } catch (Exception e) {
            throw new com.example.notificationplatform.application.notification.DeliveryException(
                    "EMAIL_SEND_FAILED",
                    e.getMessage(),
                    null,
                    e.getClass().getSimpleName(),
                    true
            );
        }
    }

    private String render(Notification notification) {
        return """
                Hello,

                %s

                Notification id: %s
                """.formatted(notification.getContent(), notification.getId());
    }
}
