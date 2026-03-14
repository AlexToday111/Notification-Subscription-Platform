package com.example.notificationplatform.infrastructure.notification;

import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.domain.subscription.Channel;
import com.example.notificationplatform.application.notification.DeliveryException;
import com.example.notificationplatform.application.notification.DeliveryResult;
import com.example.notificationplatform.application.notification.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Slf4j
@Component
public class TelegramNotificationSender implements NotificationSender{
    private final RestClient restClient;
    private final String botToken;

    public TelegramNotificationSender(RestClient.Builder builder,
                                      @Value("${app.channels.telegram.base-url:https://api.telegram.org}") String baseUrl,
                                      @Value("${app.channels.telegram.bot-token:}") String botToken) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.botToken = botToken;
    }

    @Override
    public Channel channel() {
        return Channel.TELEGRAM;
    }

    @Override
    public DeliveryResult send(Notification notification) {
        if (botToken == null || botToken.isBlank()) {
            throw new DeliveryException("TELEGRAM_NOT_CONFIGURED", "Telegram bot token is not configured", null, null, false);
        }
        try {
            String result = restClient.post()
                    .uri("/bot{token}/sendMessage", botToken)
                    .body(Map.of("chat_id", notification.getDestination(), "text", notification.getContent()))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new DeliveryException("TELEGRAM_4XX", "Telegram rejected message", response.getStatusCode().value(), "client_error", false);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new DeliveryException("TELEGRAM_5XX", "Telegram server error", response.getStatusCode().value(), "server_error", true);
                    })
                    .body(String.class);
            log.info("Telegram send result notificationId={} channel={} chatId={}",
                    notification.getId(), notification.getChannel(), notification.getDestination());
            return new DeliveryResult(200, result == null ? "ok" : result.substring(0, Math.min(result.length(), 200)));
        } catch (DeliveryException e) {
            throw e;
        } catch (RestClientException e) {
            throw new DeliveryException("TELEGRAM_NETWORK", e.getMessage(), null, e.getClass().getSimpleName(), true);
        }
    }
}
